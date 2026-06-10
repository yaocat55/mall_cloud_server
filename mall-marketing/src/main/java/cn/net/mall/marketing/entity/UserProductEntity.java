package cn.net.mall.marketing.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户产品实体
 *
 * @date 2024/9/4 下午2:52
 */
@Data
public class UserProductEntity extends BaseEntity {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(name = "用户名称")
    private String userName;

    /**
     * 商品名称
     */
    @Schema(name = "商品名称")
    private String productName;

    /**
     * 规格
     */
    @Schema(name = "规格")
    private String model;

    /**
     * 价格
     */
    @Schema(name = "价格")
    private BigDecimal price;

    /**
     * 封面
     */
    @Schema(name = "封面")
    private String coverUrl;

    /**
     * 库存
     */
    private Integer stock;
}
