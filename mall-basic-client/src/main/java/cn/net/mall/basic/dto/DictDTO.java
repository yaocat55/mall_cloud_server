package cn.net.mall.basic.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据字典实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-03-21 18:50:46
 */
@Schema(description = "数据字典实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DictDTO extends BaseEntity {


    /**
     * 父字段ID
     */
    @Schema(description = "父字段ID", example = "0")
    private Long parentId;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称", example = "-")
    private String dictName;

    /**
     * 字典描述
     */
    @Schema(description = "字典描述", example = "-")
    private String dictDescription;

    /**
     * 字典详情
     */
    @Schema(description = "字典详情", example = "string")
    private List<DictDetailDTO> detailList;
}
