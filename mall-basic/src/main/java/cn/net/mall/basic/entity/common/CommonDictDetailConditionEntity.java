package cn.net.mall.basic.entity.common;

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
@Schema(description = "部门查询条件实体")
@Data
public class CommonDictDetailConditionEntity extends RequestPageEntity {

    /**
     * 数据字典名称
     */
    @NotEmpty(message = "数据字典名称不能为空")
    @Schema(description = "数据字典名称", example = "-")
    private String dictName;


    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

    /**
     * 数据字典id
     */
    @Schema(description = "数据字典id", example = "0")
    private Long dictId;

    /**
     * 数据字典id集合
     */
    @Schema(description = "数据字典id集合", example = "0")
    private List<Long> dictIdList;

    /**
     * 值
     */
    @Schema(description = "值", example = "-")
    private String value;

    /**
     * 文本
     */
    @Schema(description = "文本", example = "标签")
    private String label;

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
}
