package cn.net.mall.basic.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息通知 DTO
 *
 * @date 2025-07-03
 */
@Schema(description = "消息通知DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonNotifyDTO extends BaseEntity {

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
}
