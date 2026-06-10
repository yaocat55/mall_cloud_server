package cn.net.mall.product.entity;

import cn.net.mall.entity.RequestPageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 商品图片查询条件实体
 *
 * @date 2024-05-09 14:43:56
 */
@Schema(name = "商品图片查询条件实体")
@Data
public class ProductPhotoConditionEntity extends RequestPageEntity {


    /**
     * ID
     */
    @Schema(name = "ID")
    private Long id;

    /**
     * 商品ID
     */
    @Schema(name = "商品ID")
    private Long productId;

    /**
     * 商品ID集合
     */
    @Schema(name = "商品ID集合")
    private List<Long> productIdList;

    /**
     * 图片名称
     */
    @Schema(name = "图片名称")
    private String name;

    /**
     * 图片url
     */
    @Schema(name = "图片url")
    private String url;

    /**
     * 排序
     */
    @Schema(name = "排序")
    private Integer sort;

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
     * 图片类型 1：封面 2：轮播图
     */
    @Schema(name = "图片类型")
    private Integer type;
}
