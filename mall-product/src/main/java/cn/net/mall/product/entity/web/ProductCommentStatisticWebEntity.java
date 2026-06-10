package cn.net.mall.product.entity.web;

import lombok.Data;

/**
 * 商品评价统计实体
 *
 * @date 2024/9/6 下午3:41
 */
@Data
public class ProductCommentStatisticWebEntity {

    /**
     * 好评率
     */
    private String positiveRating;

    /**
     * 全部评价数量
     */
    private String all = "0";

    /**
     * 好评数量
     */
    private String positive = "0";

    /**
     * 中评数量
     */
    private String moderate = "0";

    /**
     * 差评数量
     */
    private String negative = "0";
}
