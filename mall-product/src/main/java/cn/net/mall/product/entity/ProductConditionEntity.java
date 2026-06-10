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
@Schema(name = "商品查询条件实体")
@Data
public class ProductConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(name = "ID")
    private Long id;

    /**
     * ID集合
     */
    @Schema(name = "ID集合")
    private List<Long> idList;

    /**
     * 分类ID
     */
    @Schema(name = "分类ID")
    private Long categoryId;

    /**
     * 品牌ID
     */
    @Schema(name = "品牌ID")
    private Long brandId;

    /**
     * 单位ID
     */
    @Schema(name = "单位ID")
    private Long unitId;

    /**
     * 商品名称
     */
    @Schema(name = "商品名称")
    private String name;

    /**
     * 规格
     */
    @Schema(name = "规格")
    private String model;

    /**
     * hash值
     */
    @Schema(name = "hash值")
    private String hash;

    /**
     * 数量
     */
    @Schema(name = "数量")
    private Integer quantity;

    /**
     * 价格
     */
    @Schema(name = "价格")
    private BigDecimal price;

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
     * 关键字
     */
    @Schema(name = "关键字")
    private String keyword;

    /**
     * 商品查询条件
     */
    @Schema(name = "商品查询条件")
    private List<ProductEntity> productEntities;

    /**
     * 商品组ID
     */
    @Schema(name = "商品组ID")
    private Long productGroupId;

    /**
     * 商品组ID集合
     */
    @Schema(name = "商品组ID集合")
    private List<Long> productGroupIdList;

    /**
     * 逻辑删除ID，默认是0，表示未删除
     */
    private Long delId;
}
