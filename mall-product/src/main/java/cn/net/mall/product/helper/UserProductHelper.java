package cn.net.mall.product.helper;

import cn.net.mall.entity.auth.JwtUserEntity;

import cn.net.mall.product.entity.ProductConditionEntity;
import cn.net.mall.product.entity.ProductEntity;
import cn.net.mall.product.entity.UserProductEntity;
import cn.net.mall.product.mapper.ProductMapper;
import cn.net.mall.util.AssertUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.net.mall.util.FillUserUtil.getCurrentUserInfo;

/**
 * 用户商品helper
 *
 * @date 2024/9/4 下午4:09
 */
@AllArgsConstructor
@Component
public class UserProductHelper {

    private final ProductMapper productMapper;

    /**
     * 校验参数
     *
     * @param userProductEntity 用户商品实体
     */
    public void checkParam(UserProductEntity userProductEntity) {
        JwtUserEntity currentUserInfo = getCurrentUserInfo();

        ProductEntity productEntity = productMapper.findById(userProductEntity.getProductId());
        AssertUtil.notNull(productEntity, "该商品在系统中不存在");

        userProductEntity.setUserId(currentUserInfo.getId());
    }


    /**
     * 填充商品信息
     *
     * @param list 用户商品实体集合
     */
    public void fillProductInfo(List<? extends UserProductEntity> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        List<Long> productIdList = list.stream().map(UserProductEntity::getProductId).distinct().collect(Collectors.toList());
        ProductConditionEntity productConditionEntity = new ProductConditionEntity();
        productConditionEntity.setIdList(productIdList);
        List<ProductEntity> productEntities = productMapper.searchByCondition(productConditionEntity);

        if (CollectionUtils.isNotEmpty(productEntities)) {
            Map<Long, List<ProductEntity>> productMap = productEntities.stream().collect(Collectors.groupingBy(ProductEntity::getId));
            for (UserProductEntity userProductEntity : list) {
                List<ProductEntity> entityList = productMap.get(userProductEntity.getProductId());
                if (CollectionUtils.isNotEmpty(entityList)) {
                    ProductEntity productEntity = entityList.get(0);
                    userProductEntity.setModel(productEntity.getModel());
                    userProductEntity.setProductName(productEntity.getName());
                    userProductEntity.setPrice(productEntity.getPrice());
                    userProductEntity.setStock(productEntity.getQuantity());
                    userProductEntity.setCoverUrl(productEntity.getCoverUrl());
                }
            }
        }
    }
}
