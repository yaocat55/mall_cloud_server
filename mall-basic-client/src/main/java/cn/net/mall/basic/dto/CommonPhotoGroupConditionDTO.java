package cn.net.mall.basic.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 图片分组查询条件 DTO
 *
 * @date 2025-07-03
 */
@Schema(description = "图片分组查询条件DTO")
@Data
public class CommonPhotoGroupConditionDTO extends RequestConditionEntity {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "分组名称", example = "测试数据")
    private String name;

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
