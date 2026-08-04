package cn.net.mall.pay.service;

import cn.net.mall.pay.channel.PayChannelStrategy;
import cn.net.mall.pay.dto.PayChannelDTO;
import cn.net.mall.pay.entity.PayChannelConfigConditionEntity;
import cn.net.mall.pay.entity.PayChannelConfigEntity;
import cn.net.mall.pay.enums.ChannelCodeEnum;
import cn.net.mall.util.AssertUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 渠道配置加载 + 策略分发.
 *
 * <p>启动时收集所有 {@link PayChannelStrategy} 实现，按 channelCode 建 Map；
 * 调用时 {@code strategy = strategies.get(channelCode)} 分发。</p>
 *
 * <p>渠道配置从 pay_channel_config 表读取（admin 管理端维护），
 * 读取操作使用 Caffeine 缓存（writes 穿透写 DB、delete/update 淘汰缓存）。
 * 支付主流程每次调用 getStrategy() 已经是内存 Map 分发，无 DB 开销。</p>
 */
@Slf4j
@Service
public class PayChannelService {

    @Autowired
    private List<PayChannelStrategy> strategyList;

    @Autowired
    private PayChannelConfigService payChannelConfigService;

    /** 渠道策略注册表：channelCode -> strategy */
    private Map<String, PayChannelStrategy> strategyMap = new ConcurrentHashMap<>();

    /** 渠道配置 Caffeine 缓存：TTL 10min，写穿透，admin 更新时主动淘汰 */
    private final Cache<String, PayChannelConfigEntity> configCache = Caffeine.newBuilder()
            .maximumSize(32)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build();

    @PostConstruct
    public void init() {
        strategyMap = strategyList.stream()
                .collect(Collectors.toMap(PayChannelStrategy::channelCode, Function.identity()));
        log.info("支付渠道策略注册完成: {}", strategyMap.keySet());
    }

    /**
     * 获取渠道策略，不存在则抛异常.
     */
    public PayChannelStrategy getStrategy(String channelCode) {
        PayChannelStrategy strategy = strategyMap.get(channelCode);
        AssertUtil.notNull(strategy, "不支持的支付渠道: " + channelCode);
        return strategy;
    }

    /**
     * 获取启用中的渠道配置（Caffeine 缓存，key = channelCode）.
     *
     * <p>缓存未命中时查 DB 并回填；admin 管理端修改/删除渠道配置后调用
     * {@link #evictConfig(String)} 淘汰缓存。</p>
     */
    public PayChannelConfigEntity getConfig(String channelCode) {
        ChannelCodeEnum channel = ChannelCodeEnum.from(channelCode);
        AssertUtil.notNull(channel, "不支持的支付渠道: " + channelCode);

        return configCache.get(channelCode, code -> {
            log.info("渠道配置缓存未命中，查 DB: channelCode={}", channelCode);
            PayChannelConfigConditionEntity condition = new PayChannelConfigConditionEntity();
            condition.setChannelCode(channelCode);
            condition.setStatus(1);
            List<PayChannelConfigEntity> configs = payChannelConfigService.searchByPage(condition).getData();
            AssertUtil.notEmpty(configs, "支付渠道未配置或已禁用: " + channelCode);
            return configs.get(0);
        });
    }

    /**
     * 淘汰指定渠道的配置缓存（admin 端 insert/update/delete 时调用）.
     */
    public void evictConfig(String channelCode) {
        configCache.invalidate(channelCode);
        log.info("渠道配置缓存已淘汰: channelCode={}", channelCode);
    }

    /**
     * 查询启用中的全部支付渠道（前端收银台渲染支付方式用）.
     *
     * <p>只返回展示所需字段（编码/名称），不暴露任何密钥。</p>
     */
    public List<PayChannelDTO> listAvailableChannels() {
        PayChannelConfigConditionEntity condition = new PayChannelConfigConditionEntity();
        condition.setStatus(1);
        return payChannelConfigService.searchByPage(condition).getData().stream()
                .map(config -> {
                    PayChannelDTO dto = new PayChannelDTO();
                    dto.setChannelCode(config.getChannelCode());
                    dto.setChannelName(config.getChannelName());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
