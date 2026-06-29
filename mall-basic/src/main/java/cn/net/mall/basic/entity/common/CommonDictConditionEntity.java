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
@Schema(description = "数据字典查询条件实体")
@Data
public class CommonDictConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

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
     * 创建人ID
     */
    @Schema(description = "创建人ID", example = "1")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @Schema(description = "创建人名称", example = "admin")
    private String createUserName;

    /**
     * 创建日期
     */
    @Schema(description = "创建日期", example = "2024-01-01 00:00:00")
    private Date createTime;

    /**
     * 修改人ID
     */
    @Schema(description = "修改人ID", example = "1")
    private Long updateUserId;

    /**
     * 修改人名称
     */
    @Schema(description = "修改人名称", example = "admin")
    private String updateUserName;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间", example = "2024-01-01 00:00:00")
    private Date updateTime;

    /**
     * 是否删除 1：已删除 0：未删除
     */
    @Schema(description = "是否删除 1：已删除 0：未删除", example = "0")
    private Integer isDel;

    /**
     * 查询条件
     */
    @Schema(description = "查询条件", example = "-")
    private String blurry;
}
