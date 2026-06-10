package cn.net.mall.message.entity;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

@Data
public class CommonNotifyConditionEntity extends RequestConditionEntity {
    private Long id;
    private Integer type;
    private String title;
    private String content;
    private String linkUrl;
    private Integer readStatus;
    private Long toUserId;
    private Integer isPush;
    private Long createUserId;
    private String createUserName;
}
