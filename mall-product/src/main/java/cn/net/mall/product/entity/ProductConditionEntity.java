package cn.net.mall.product.entity;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品查询条件实体
 *
 * @date 2024-05-09 14:43:56
 */
@Schema(description = "商品查询条件实体")
@Data
public class ProductConditionEntity extends RequestConditionEntity {


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
     * 分类ID
     */
    @Schema(description = "分类ID", example = "0")
    private Long categoryId;

    /**
     * 品牌ID
     */
    @Schema(description = "品牌ID", example = "0")
    private Long brandId;

    /**
     * 单位ID
     */
    @Schema(description = "单位ID", example = "0")
    private Long unitId;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称", example = "测试数据")
    private String name;

    /**
     * 规格
     */
    @Schema(description = "规格", example = "型号")
    private String model;

    /**
     * hash值
     */
    @Schema(description = "hash值", example = "-")
    private String hash;

    /**
     * 数量
     */
    @Schema(description = "数量", example = "10")
    private Integer quantity;

    /**
     * 价格
     */
    @Schema(description = "价格", example = "99.99")
    private BigDecimal price;

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
     * 关键字
     */
    @Schema(description = "关键字", example = "-")
    private String keyword;

    /**
     * 商品查询条件
     */
    @Schema(description = "商品查询条件")
    private List<ProductEntity> productEntities;

    /**
     * 商品组ID
     */
    @Schema(description = "商品组ID", example = "0")
    private Long productGroupId;

    /**
     * 商品组ID集合
     */
    @Schema(description = "商品组ID集合", example = "0")
    private List<Long> productGroupIdList;

    /**
     * 逻辑删除ID，默认是0，表示未删除
     */
    private Long delId;
}
