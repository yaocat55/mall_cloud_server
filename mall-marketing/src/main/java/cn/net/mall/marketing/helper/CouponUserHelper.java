package cn.net.mall.marketing.helper;

import cn.net.mall.auth.client.UserFeignClient;
import cn.net.mall.auth.dto.UserDTO;
import cn.net.mall.marketing.entity.CouponConditionEntity;
import cn.net.mall.marketing.entity.CouponEntity;
import cn.net.mall.marketing.entity.CouponUserEntity;
import cn.net.mall.marketing.entity.UserProductEntity;
import cn.net.mall.marketing.mapper.CouponMapper;
import cn.net.mall.product.client.ProductFeignClient;
import cn.net.mall.product.dto.ProductDTO;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 优惠券用户helper
 *
 * @date 2024/9/13 下午6:34
 */
@AllArgsConstructor
@Component
public class CouponUserHelper {

    private final CouponMapper couponMapper;
    private final ProductFeignClient productFeignClient;
    private final UserFeignClient userFeignClient;

    /**
     * 填充优惠券和用户信息
     *
     * @param list 优惠券用户实体集合
     */
    public void fillCouponUserInfo(List<? extends CouponUserEntity> list) {
        fillCouponInfo(list);
        fillUserInfo(list);
        fillProductInfo(list);
    }

    private void fillProductInfo(List<? extends CouponUserEntity> list) {
        List<Long> productIdList = list.stream().map(CouponUserEntity::getProductId).distinct().collect(Collectors.toList());
        List<ProductDTO> productEntities = productFeignClient.findByIds(productIdList);
        if (CollectionUtils.isNotEmpty(productEntities)) {
            Map<Long, List<ProductDTO>> productMap = productEntities.stream().collect(Collectors.groupingBy(ProductDTO::getId));
            for (UserProductEntity userProductEntity : list) {
                List<ProductDTO> entityList = productMap.get(userProductEntity.getProductId());
                if (CollectionUtils.isNotEmpty(entityList)) {
                    ProductDTO productEntity = entityList.get(0);
                    userProductEntity.setModel(productEntity.getModel());
                    userProductEntity.setProductName(productEntity.getName());
                    userProductEntity.setPrice(productEntity.getPrice());
                    userProductEntity.setStock(productEntity.getQuantity());
                    userProductEntity.setCoverUrl(productEntity.getCoverUrl());
                }
            }
        }
    }

    /**
     * 填充用户信息
     *
     * @param list 用户集合
     */
    public void fillUserInfo(List<? extends UserProductEntity> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        List<Long> userIdList = list.stream().filter(x -> x.getUserId() > 0)
                .map(UserProductEntity::getUserId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIdList)) {
            return;
        }
        List<UserDTO> userEntities = userFeignClient.findByIds(userIdList);
        if (CollectionUtils.isNotEmpty(userEntities)) {
            Map<Long, List<UserDTO>> userMap = userEntities.stream().collect(Collectors.groupingBy(UserDTO::getId));
            for (UserProductEntity userProductEntity : list) {
                List<UserDTO> entityList = userMap.get(userProductEntity.getUserId());
                if (CollectionUtils.isNotEmpty(entityList)) {
                    userProductEntity.setUserName(entityList.get(0).getUserName());
                }
            }
        }
    }


    /**
     * 填充优惠券信息
     *
     * @param list 优惠券集合
     */
    public void fillCouponInfo(List<? extends CouponUserEntity> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        List<Long> couponIdList = list.stream().map(CouponUserEntity::getCouponId).distinct().collect(Collectors.toList());
        CouponConditionEntity couponConditionEntity = new CouponConditionEntity();
        couponConditionEntity.setIdList(couponIdList);
        List<CouponEntity> couponEntities = couponMapper.searchByCondition(couponConditionEntity);
        for (CouponUserEntity couponUserEntity : list) {
            Optional<CouponEntity> couponEntityOptional = couponEntities.stream().filter(x -> x.getId().equals(couponUserEntity.getCouponId())).findAny();
            if (couponEntityOptional.isPresent()) {
                couponUserEntity.setCouponName(couponEntityOptional.get().getName());
            }
        }
    }
}
