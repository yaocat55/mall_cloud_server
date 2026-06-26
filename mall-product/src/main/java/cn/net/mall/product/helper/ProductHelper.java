package cn.net.mall.product.helper;

import cn.net.mall.workid.IdGenerateHelper;
import cn.net.mall.product.entity.ProductConditionEntity;
import cn.net.mall.product.entity.ProductEntity;
import cn.net.mall.product.mapper.ProductMapper;
import cn.net.mall.util.AssertUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.util.List;
import java.util.Objects;

/**
 * 商品帮助类
 *
 * @date 2024/5/12 下午8:52
 */
@Component
public class ProductHelper {

    private final ProductMapper productMapper;
    private final IdGenerateHelper idGenerateHelper;

    public ProductHelper(ProductMapper productMapper, IdGenerateHelper idGenerateHelper) {
        this.productMapper = productMapper;
        this.idGenerateHelper = idGenerateHelper;
    }

    /**
     * 批量insert商品
     *
     * @param productEntities 商品列表
     */
    public void batchInsert(List<ProductEntity> productEntities) {
        AssertUtil.notEmpty(productEntities, "商品数据不能为空");

        for (ProductEntity productEntity : productEntities) {
            doInsert(productEntity);
        }
    }

    private void doInsert(ProductEntity productEntity) {
        ProductEntity oldProductEntity = queryOldProductEntity(productEntity);
        if (Objects.nonNull(oldProductEntity)) {
            productEntity.setId(oldProductEntity.getId());
            return;
        }

        try {
            productEntity.setId(idGenerateHelper.nextId());
            productEntity.setProductGroupId(productEntity.getProductGroupEntity().getId());
            productMapper.batchInsert(Lists.newArrayList(productEntity));
            productEntity.setIsNew(true);
        } catch (DuplicateKeyException e) {
            oldProductEntity = queryOldProductEntity(productEntity);
            AssertUtil.notNull(oldProductEntity, "创建商品失败，请稍后重试");
            productEntity.setId(oldProductEntity.getId());
        }
    }


    private ProductEntity queryOldProductEntity(ProductEntity productEntity) {
        ProductConditionEntity productConditionEntity = new ProductConditionEntity();
        productConditionEntity.setCategoryId(productEntity.getCategoryId());
        productConditionEntity.setProductGroupId(productEntity.getProductGroupEntity().getId());
        productConditionEntity.setUnitId(productEntity.getUnitId());
        productConditionEntity.setBrandId(productEntity.getBrandId());
        productConditionEntity.setHash(productEntity.getHash());
        productConditionEntity.setPageSize(1);
        List<ProductEntity> productEntities = productMapper.searchByCondition(productConditionEntity);
        if (CollectionUtils.isNotEmpty(productEntities)) {
            return productEntities.get(0);
        }
        return null;
    }
}
