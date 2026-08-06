package cn.net.mall.mq.core;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.Map;

// MQ 发送选项，通过 Builder 组装，避免方法重载爆炸。
@Data
@Builder
public class MqSendOptions {

    // tag 消息子类型过滤，空 = 不设置
    @Builder.Default
    private String tag = "";

    // keys 业务键，用于消息查询/追踪
    @Builder.Default
    private String keys = "";

    // 延时等级 1=1s .. 18=2h
    private Integer delayLevel;

    // 同步发送超时 ms，默认 3000
    private Long timeout;

    // 自定义属性，打入消息头，消费端可用 SQL92 过滤
    @Builder.Default
    private Map<String, String> properties = Collections.emptyMap();

    public static MqSendOptions defaults() {
        return MqSendOptions.builder().build();
    }

    public static MqSendOptions ofTag(String tag) {
        return MqSendOptions.builder().tag(tag).build();
    }

    public static MqSendOptions of(String tag, String keys) {
        return MqSendOptions.builder().tag(tag).keys(keys).build();
    }
}
