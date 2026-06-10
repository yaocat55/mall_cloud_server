package cn.net.mall.product.helper;

import cn.net.mall.product.entity.BrandConditionEntity;
import cn.net.mall.product.entity.BrandEntity;
import cn.net.mall.product.entity.ProductEntity;
import cn.net.mall.product.mapper.BrandMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 品牌 helper
 *
 * @date 2024/9/8 下午1:47
 */
@Component
public class BrandHelper {

    private final BrandMapper brandMapper;

    public BrandHelper(BrandMapper brandMapper) {
        this.brandMapper = brandMapper;
    }

    /**
     * 添加品牌信息
     *
     * @param list 商品信息
     */
    public void fillBrand(List<ProductEntity> list) {
        List<Long> brandSysNoList = list.stream().map(ProductEntity::getBrandId).distinct().collect(Collectors.toList());
        BrandConditionEntity brandConditionEntity = new BrandConditionEntity();
        brandConditionEntity.setIdList(brandSysNoList);
        List<BrandEntity> brandEntities = brandMapper.searchByCondition(brandConditionEntity);
        if (CollectionUtils.isEmpty(brandEntities)) {
            return;
        }

        Map<Long, List<BrandEntity>> brandMap = brandEntities.stream().collect(Collectors.groupingBy(BrandEntity::getId));
        for (ProductEntity productEntity : list) {
            List<BrandEntity> findBrandList = brandMap.get(productEntity.getBrandId());
            if (CollectionUtils.isEmpty(findBrandList)) {
                continue;
            }
            productEntity.setBrandName(findBrandList.get(0).getName());
        }
    }
}
