package cn.net.mall.recommend.message;

import cn.net.mall.mq.consumer.AbstractMqConsumer;
import cn.net.mall.mq.core.MqMessage;
import cn.net.mall.product.dto.ProductViewRecordDTO;
import cn.net.mall.recommend.entity.ProductViewRecordConditionEntity;
import cn.net.mall.recommend.entity.ProductViewRecordEntity;
import cn.net.mall.recommend.mapper.ProductViewRecordMapper;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.redis.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RocketMQMessageListener(topic = "RECOMMEND_PRODUCT_VIEW_TOPIC", consumerGroup = "recommend_product_view_group")
public class ProductViewRecordConsumer extends AbstractMqConsumer<ProductViewRecordDTO> {

    private final ProductViewRecordMapper productViewRecordMapper;
    private final RedisUtil redisUtil;

    public ProductViewRecordConsumer(ProductViewRecordMapper productViewRecordMapper,
                                      RedisUtil redisUtil,
                                      ObjectMapper objectMapper) {
        super(ProductViewRecordDTO.class, objectMapper);
        this.productViewRecordMapper = productViewRecordMapper;
        this.redisUtil = redisUtil;
    }

    @Override
    protected void onPayload(ProductViewRecordDTO msg, MqMessage<ProductViewRecordDTO> envelope, MessageExt raw) {
        if (msg == null || msg.getUserId() == null || msg.getProductId() == null) {
            return;
        }
        String dedupKey = "recommend:productView:msg:" + (msg.getId() == null ? (msg.getUserId() + ":" + msg.getProductId()) : msg.getId());
        boolean first = redisUtil.setIfAbsent(dedupKey, "1");
        if (!first) {
            log.warn("[MQ] 重复浏览消息忽略 msgId={} key={} userId={} productId={}",
                    envelope.getMsgId(), dedupKey, msg.getUserId(), msg.getProductId());
            return;
        }

        ProductViewRecordConditionEntity condition = new ProductViewRecordConditionEntity();
        condition.setUserId(msg.getUserId());
        condition.setProductId(msg.getProductId());
        List<ProductViewRecordEntity> list = productViewRecordMapper.searchByCondition(condition);
        if (CollectionUtils.isNotEmpty(list)) {
            ProductViewRecordEntity entity = list.get(0);
            FillUserUtil.fillUpdateDefaultUserInfo(entity);
            entity.setViewCount((entity.getViewCount() == null ? 0 : entity.getViewCount()) + Math.max(1, msg.getViewCount()));
            productViewRecordMapper.update(entity);
            log.info("[MQ] 更新浏览记录 msgId={} userId={} productId={} viewCount={}",
                    envelope.getMsgId(), entity.getUserId(), entity.getProductId(), entity.getViewCount());
        } else {
            ProductViewRecordEntity entity = new ProductViewRecordEntity();
            entity.setUserId(msg.getUserId());
            entity.setProductId(msg.getProductId());
            entity.setViewCount(Math.max(1, msg.getViewCount()));
            FillUserUtil.fillCreateDefaultUserInfo(entity);
            productViewRecordMapper.insert(entity);
            log.info("[MQ] 新增浏览记录 msgId={} userId={} productId={} viewCount={}",
                    envelope.getMsgId(), entity.getUserId(), entity.getProductId(), entity.getViewCount());
        }
    }
}
