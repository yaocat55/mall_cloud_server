package cn.net.mall.product.util;


import cn.net.mall.product.entity.ProductPhotoEntity;

import java.util.List;
import java.util.Optional;

/**
 * 获取封面图片
 *
 * @date 2024/8/27 下午6:42
 */
public abstract class CoverUtil {

    /**
     * 从商品图片列表中获取指定商品的封面图片
     *
     * @param productId            商品ID
     * @param productPhotoEntities 商品图片列表
     * @return 封面图片
     */
    public static String getCover(Long productId, List<ProductPhotoEntity> productPhotoEntities) {
        Optional<ProductPhotoEntity> photoEntityOptional = productPhotoEntities.stream()
                .filter(x -> x.getProductId().equals(productId)).findAny();
        if (photoEntityOptional.isPresent()) {
            return photoEntityOptional.get().getUrl();
        }
        return null;
    }
}
