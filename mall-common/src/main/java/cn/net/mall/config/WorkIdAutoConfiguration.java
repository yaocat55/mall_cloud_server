package cn.net.mall.config;

import cn.net.mall.workid.IdGenerateHelper;
import cn.net.mall.workid.SnowFlakeIdWorker;
import cn.net.mall.workid.WorkIdAllocator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 雪花算法分布式 ID 自动配置。
 *
 * <p>条件：classpath 上存在 {@code org.springframework.data.redis.core.RedisTemplate}</p>
 *
 * <p>配置属性：</p>
 * <ul>
 *   <li>{@code spring.application.name}（用于 WorkerId 命名，默认：空）</li>
 *   <li>{@code work.id.idGenerator.redis.ttl}（WorkerId TTL，默认：3600 秒）</li>
 *   <li>{@code work.id.idGenerator.redis.heartBeatIntervalSecond}（心跳间隔，默认：60 秒）</li>
 * </ul>
 *
 * <p>注册的 Bean：</p>
 * <ul>
 *   <li>{@link SnowFlakeIdWorker} — 雪花算法 ID 生成器</li>
 *   <li>{@link IdGenerateHelper} — ID 生成服务封装</li>
 *   <li>{@link WorkIdAllocator} — 基于 Redis 的 WorkerId 分配器（@PostConstruct 自动分配）</li>
 * </ul>
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
public class WorkIdAutoConfiguration {

    /**
     * SnowFlakeIdWorker —— 雪花算法 ID 生成器。
     * <p>使用默认的 dataCenterId=0 和 workerId=0 初始化，实际的 workerId 将由
     * {@link WorkIdAllocator} 在 {@code @PostConstruct} 阶段通过 Redis 自动分配并覆盖。</p>
     *
     * @return SnowFlakeIdWorker 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SnowFlakeIdWorker snowFlakeIdWorker() {
        return new SnowFlakeIdWorker(0, 0);
    }

    /**
     * IdGenerateHelper —— ID 生成服务封装。
     * <p>在 {@link SnowFlakeIdWorker} 基础上提供更便捷的 ID 生成方法，包括
     * 分布式唯一 ID、指定前缀 ID 等。</p>
     *
     * @param snowFlakeIdWorker SnowFlakeIdWorker 实例
     * @return IdGenerateHelper 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public IdGenerateHelper idGenerateHelper(SnowFlakeIdWorker snowFlakeIdWorker) {
        return new IdGenerateHelper(snowFlakeIdWorker);
    }

    /**
     * WorkIdAllocator —— 基于 Redis 的 WorkerId 分配器。
     * <p>在 {@code @PostConstruct} 初始化阶段自动从 Redis 申请可用的 workerId，
     * 并通过心跳机制维持租约。服务关闭时自动释放 workerId。</p>
     *
     * @param redisTemplate      RedisTemplate 实例，用于操作 Redis
     * @param snowFlakeIdWorker  SnowFlakeIdWorker 实例，将为其设置分配的 workerId
     * @return WorkIdAllocator 实例
     */
    @Bean
    @Lazy
    @ConditionalOnMissingBean
    public WorkIdAllocator workIdAllocator(RedisTemplate<String, String> redisTemplate,
                                            SnowFlakeIdWorker snowFlakeIdWorker) {
        return new WorkIdAllocator(redisTemplate, snowFlakeIdWorker);
    }
}
