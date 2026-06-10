package cn.net.mall.basic.dto;

import cn.net.mall.entity.RequestPageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 部门查询条件实体
 *
 * @date 2024-03-25 21:41:03
 */
@Schema(name = "部门查询条件实体")
@Data
public class DictDetailConditionDTO extends RequestPageEntity {

    /**
     * 数据字典名称
     */
    @NotEmpty(message = "数据字典名称不能为空")
    @Schema(name = "数据字典名称")
    private String dictName;


    /**
     * ID
     */
    @Schema(name = "ID")
    private Long id;

    /**
     * 数据字典id
     */
    @Schema(name = "数据字典id")
    private Long dictId;

    /**
     * 数据字典id集合
     */
    @Schema(name = "数据字典id集合")
    private List<Long> dictIdList;

    /**
     * 值
     */
    @Schema(name = "值")
    private String value;

    /**
     * 文本
     */
    @Schema(name = "文本")
    private String label;

    /**
     * 创建人ID
     */
    @Schema(name = "创建人ID")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @Schema(name = "创建人名称")
    private String createUserName;

    /**
     * 创建日期
     */
    @Schema(name = "创建日期")
    private Date createTime;

    /**
     * 修改人ID
     */
    @Schema(name = "修改人ID")
    private Long updateUserId;

    /**
     * 修改人名称
     */
    @Schema(name = "修改人名称")
    private String updateUserName;

    /**
     * 修改时间
     */
    @Schema(name = "修改时间")
    private Date updateTime;

    /**
     * 是否删除 1：已删除 0：未删除
     */
    @Schema(name = "是否删除 1：已删除 0：未删除")
    private Integer isDel;
}
