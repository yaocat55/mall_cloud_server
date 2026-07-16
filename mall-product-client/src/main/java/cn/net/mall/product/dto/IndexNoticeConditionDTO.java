package cn.net.mall.product.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 首页公告查询条件 DTO
 *
 * @date 2025/07/15
 */
@Schema(description = "首页公告查询条件DTO")
@Data
public class IndexNoticeConditionDTO extends RequestConditionEntity {

    @Schema(description = "系统ID列表")
    private List<Long> idList;

    @Schema(description = "系统ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "创建人ID")
    private Long createUserId;

    @Schema(description = "创建人名称")
    private String createUserName;

    @Schema(description = "修改人ID")
    private Long updateUserId;

    @Schema(description = "修改人名称")
    private String updateUserName;

    @Schema(description = "是否删除 1：已删除 0：未删除")
    private Integer isDel;
}
