package cn.net.mall.basic.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 消息通知查询条件 DTO
 *
 * @date 2025-07-03
 */
@Schema(description = "消息通知查询条件DTO")
@Data
public class CommonNotifyConditionDTO extends RequestConditionEntity {

    @Schema(description = "系统ID")
    private Long id;

    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "link Url")
    private String linkUrl;

    @Schema(description = "read Status")
    private Integer readStatus;

    @Schema(description = "to User Id")
    private Long toUserId;

    @Schema(description = "is Push")
    private Integer isPush;

    @Schema(description = "create User Id")
    private Long createUserId;

    @Schema(description = "create User Name")
    private String createUserName;
}
