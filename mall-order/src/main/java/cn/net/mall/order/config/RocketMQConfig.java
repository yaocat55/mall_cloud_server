package cn.net.mall.order.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.name-server:}")
    private String nameServer;

    @Value("${rocketmq.producer.group:mall-order-api-producer}")
    private String producerGroup;

    @Value("${rocketmq.producer.send-message-timeout:3000}")
    private int sendTimeout;

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(DefaultMQProducer.class)
    public DefaultMQProducer defaultMQProducer() {
        if (nameServer == null || nameServer.isBlank()) {
            log.error("加载RocketMQ配置失败：name-server为空，请在配置文件中设置 rocketmq.name-server");
        } else {
            log.info("初始化DefaultMQProducer，nameServer={}, group={}, sendTimeout={}", nameServer, producerGroup, sendTimeout);
        }
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(nameServer);
        producer.setSendMsgTimeout(sendTimeout);
        producer.setRetryTimesWhenSendFailed(1);
        return producer;
    }

    @Bean
    @ConditionalOnMissingBean(RocketMQTemplate.class)
    public RocketMQTemplate rocketMQTemplate(DefaultMQProducer producer, ObjectMapper objectMapper) {
        RocketMQTemplate template = new RocketMQTemplate();
        template.setProducer(producer);
        MappingJackson2MessageConverter jacksonConverter = new MappingJackson2MessageConverter();
        jacksonConverter.setObjectMapper(objectMapper);
        template.setMessageConverter(jacksonConverter);
        log.info("RocketMQTemplate创建完成并设置MappingJackson2MessageConverter");
        return template;
    }
}
