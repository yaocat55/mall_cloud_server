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
}
