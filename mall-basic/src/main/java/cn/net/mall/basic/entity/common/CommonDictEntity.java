package cn.net.mall.basic.entity.common;

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
@Schema(name = "数据字典实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonDictEntity extends BaseEntity {


    /**
     * 父字段ID
     */
    @Schema(name = "父字段ID")
    private Long parentId;

    /**
     * 字典名称
     */
    @Schema(name = "字典名称")
    private String dictName;

    /**
     * 字典描述
     */
    @Schema(name = "字典描述")
    private String dictDescription;

    /**
     * 字典详情
     */
    @Schema(name = "字典详情")
    private List<CommonDictDetailEntity> detailList;
}
