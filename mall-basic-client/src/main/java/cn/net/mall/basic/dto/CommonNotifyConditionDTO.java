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

    @Schema(description = "系统ID", example = "0")
    private Long id;

    @Schema(description = "类型", example = "0")
    private Integer type;

    @Schema(description = "标题", example = "string")
    private String title;

    @Schema(description = "内容", example = "string")
    private String content;

    @Schema(description = "link Url", example = "string")
    private String linkUrl;

    @Schema(description = "read Status", example = "0")
    private Integer readStatus;

    @Schema(description = "to User Id", example = "0")
    private Long toUserId;

    @Schema(description = "is Push", example = "0")
    private Integer isPush;

    @Schema(description = "create User Id", example = "0")
    private Long createUserId;

    @Schema(description = "create User Name", example = "string")
    private String createUserName;
}
