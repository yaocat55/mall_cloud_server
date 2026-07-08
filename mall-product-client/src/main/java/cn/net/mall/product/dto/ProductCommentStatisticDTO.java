package cn.net.mall.product.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品评价统计实体
 *
 * @date 2024/9/6 下午3:41
 */
@Data
@Schema(description = "商品评论统计")

public class ProductCommentStatisticDTO {

    /**
     * 好评率
     */
    @Schema(description = "好评率", example = "string")
    private String positiveRating;

    /**
     * 全部评价数量
     */
    @Schema(description = "all", example = "string")
    private String all = "0";

    /**
     * 好评数量
     */
    @Schema(description = "positive", example = "string")
    private String positive = "0";

    /**
     * 中评数量
     */
    @Schema(description = "moderate", example = "string")
    private String moderate = "0";

    /**
     * 差评数量
     */
    @Schema(description = "negative", example = "string")
    private String negative = "0";
}
