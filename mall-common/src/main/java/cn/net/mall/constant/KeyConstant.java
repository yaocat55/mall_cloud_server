package cn.net.mall.constant;

/**
 * key常量
 *
 * @date 2024/7/14 下午7:44
 */
public abstract class KeyConstant {

    private KeyConstant() {

    }

    /**
     * 秒杀商品详情Redis中key的前缀
     */
    public static final String SECKILL_PRODUCT_DETAIL_PFREFIX = "seckillProductDetail:";

    /**
     * 秒杀商品库存
     */
    public static final String SECKILL_PRODUCT_STOCK_PREFIX = "seckillProductStock:";
}
