package cn.net.mall.basic.entity.common;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 数据字典查询条件实体
 *
 * @date 2024-03-21 18:50:46
 */
@Schema(name = "数据字典查询条件实体")
@Data
public class CommonDictConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(name = "ID")
    private Long id;

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

    /**
     * 查询条件
     */
    @Schema(name = "查询条件")
    private String blurry;
}
