package cn.net.mall.message.entity;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "CommonNotify信息")

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
