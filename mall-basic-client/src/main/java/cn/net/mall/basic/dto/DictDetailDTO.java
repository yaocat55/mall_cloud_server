package cn.net.mall.basic.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 部门实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-03-25 21:41:03
 */
@Schema(description = "部门实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DictDetailDTO extends BaseEntity {


    /**
     * 数据字典id
     */
    @Schema(description = "数据字典id", example = "0")
    private Long dictId;

    /**
     * 值
     */
    @Schema(description = "值", example = "-")
    private String value;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    private Integer sort;

    /**
     * 文本
     */
    @Schema(description = "文本", example = "标签")
    private String label;

    /**
     * 数据字典
     */
    @Schema(description = "数据字典")
    private DictDTO dict;
}
