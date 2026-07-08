package cn.net.mall.product.dto;

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
public class UserProductDTO extends BaseEntity {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", example = "0")
    private Long productId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "0")
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称", example = "admin")
    private String userName;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称", example = "-")
    private String productName;

    /**
     * 规格
     */
    @Schema(description = "规格", example = "型号")
    private String model;

    /**
     * 价格
     */
    @Schema(description = "价格", example = "99.99")
    private BigDecimal price;

    /**
     * 封面
     */
    @Schema(description = "封面", example = "https://example.com/cover.png")
    private String coverUrl;

    /**
     * 库存
     */
    @Schema(description = "库存", example = "0")
    private Integer stock;
}
