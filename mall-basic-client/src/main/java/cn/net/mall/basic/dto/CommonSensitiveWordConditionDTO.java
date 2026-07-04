package cn.net.mall.basic.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 敏感词查询条件 DTO
 *
 * @date 2025-07-03
 */
@Schema(description = "敏感词查询条件DTO")
@Data
public class CommonSensitiveWordConditionDTO extends RequestConditionEntity {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "类型 1:广告 2:政治 3：违法 4：色情 5：网址", example = "1")
    private Integer type;

    @Schema(description = "名称", example = "-")
    private String word;

    @Schema(description = "创建人ID", example = "1")
    private Long createUserId;

    @Schema(description = "创建人名称", example = "admin")
    private String createUserName;

    @Schema(description = "创建日期", example = "2024-01-01 00:00:00")
    private Date createTime;

    @Schema(description = "修改人ID", example = "1")
    private Long updateUserId;

    @Schema(description = "修改人名称", example = "admin")
    private String updateUserName;

    @Schema(description = "修改时间", example = "2024-01-01 00:00:00")
    private Date updateTime;

    @Schema(description = "是否删除 1：已删除 0：未删除", example = "0")
    private Integer isDel;
}
