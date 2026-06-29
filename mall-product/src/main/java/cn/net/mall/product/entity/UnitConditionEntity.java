package cn.net.mall.product.entity;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 单位查询条件实体
 *
 * @date 2024-05-09 14:43:55
 */
@Schema(description = "单位查询条件实体")
@Data
public class UnitConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

    /**
     * ID集合
     */
    @Schema(description = "ID集合", example = "0")
    private List<Long> idList;

    /**
     * 单位名称
     */
    @Schema(description = "单位名称", example = "测试数据")
    private String name;

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
