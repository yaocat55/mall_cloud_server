package cn.net.mall.message.entity;

import cn.net.mall.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "CommonNotify信息")

public class CommonNotifyEntity extends BaseEntity {
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
