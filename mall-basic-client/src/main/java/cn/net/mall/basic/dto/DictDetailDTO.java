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
@Schema(name = "部门实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DictDetailDTO extends BaseEntity {


    /**
     * 数据字典id
     */
    @Schema(name = "数据字典id")
    private Long dictId;

    /**
     * 值
     */
    @Schema(name = "值")
    private String value;

    /**
     * 排序
     */
    @Schema(name = "排序")
    private Integer sort;

    /**
     * 文本
     */
    @Schema(name = "文本")
    private String label;

    /**
     * 数据字典
     */
    @Schema(name = "数据字典")
    private DictDTO dict;
}
