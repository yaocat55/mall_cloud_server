package cn.net.mall.product.entity.web;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品评价统计实体
 *
 * @date 2024/9/6 下午3:41
 */
@Data
@Schema(description = "ProductCommentStatistic信息")

public class ProductCommentStatisticWebEntity {

    /**
     * 好评率
     */
    @Schema(description = "positive Rating")
    private String positiveRating;

    /**
     * 全部评价数量
     */
    @Schema(description = "all")
    private String all = "0";

    /**
     * 好评数量
     */
    @Schema(description = "positive")
    private String positive = "0";

    /**
     * 中评数量
     */
    @Schema(description = "moderate")
    private String moderate = "0";

    /**
     * 差评数量
     */
    @Schema(description = "negative")
    private String negative = "0";
}
