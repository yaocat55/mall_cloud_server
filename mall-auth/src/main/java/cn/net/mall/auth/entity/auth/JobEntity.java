package cn.net.mall.auth.entity.auth;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 岗位实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-01-08 17:18:17
 */
@Schema(description = "岗位实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JobEntity extends BaseEntity {


    /**
     * 岗位名称
     */
    @Schema(description = "岗位名称", example = "测试数据")
    private String name;

    /**
     * 岗位排序
     */
    @Schema(description = "岗位排序", example = "1")
    private Integer sort;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    /**
     * 有效状态 1:有效 0:无效
     */
    @Schema(description = "有效状态 1:有效 0:无效", example = "1")
    private Boolean validStatus;
}
