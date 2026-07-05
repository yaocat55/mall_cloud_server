package cn.net.mall.product.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 单位查询条件 DTO
 * 
* 用于 mall-admin-api 通过 Feign 调用查询单位列表
 */
@Schema(description = "单位查询条件 DTO")
@Data
public class UnitConditionDTO extends RequestConditionEntity {

    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

    /**
     * ID集合
     */
    @Schema(description = "ID集合")
    private List<Long> idList;

    /**
     * 单位名称
     */
    @Schema(description = "单位名称", example = "测试数据")
    private String name;

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
     * 修改时间
     */
    @Schema(description = "修改时间", example = "2024-01-01 00:00:00")
    private Date updateTime;
}
