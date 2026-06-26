package cn.net.mall.product.helper;

import cn.net.mall.workid.IdGenerateHelper;
import cn.net.mall.product.entity.ProductGroupConditionEntity;
import cn.net.mall.product.entity.ProductGroupEntity;
import cn.net.mall.product.mapper.ProductGroupMapper;
import cn.net.mall.util.AssertUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.util.List;
import java.util.Objects;

/**
 * 商品组helper
 *
 * @date 2024/9/8 上午11:46
 */
@Component
public class ProductGroupHelper {

    private final ProductGroupMapper productGroupMapper;
    private final IdGenerateHelper idGenerateHelper;

    public ProductGroupHelper(ProductGroupMapper productGroupMapper, IdGenerateHelper idGenerateHelper) {
        this.productGroupMapper = productGroupMapper;
        this.idGenerateHelper = idGenerateHelper;
    }

    /**
     * 批量insert商品组
     *
     * @param productGroupEntities 商品组列表
     */
    public void batchInsert(List<ProductGroupEntity> productGroupEntities) {
        AssertUtil.notEmpty(productGroupEntities, "商品组数据不能为空");

        for (ProductGroupEntity productGroupEntity : productGroupEntities) {
            doInsert(productGroupEntity);
        }
    }

    private void doInsert(ProductGroupEntity productGroupEntity) {
        ProductGroupEntity oldProductGroupEntity = queryOldProductGroupEntity(productGroupEntity);
        if (Objects.nonNull(oldProductGroupEntity)) {
            productGroupEntity.setId(oldProductGroupEntity.getId());
            return;
        }

        try {
            productGroupEntity.setId(idGenerateHelper.nextId());
            productGroupMapper.batchInsert(Lists.newArrayList(productGroupEntity));
            productGroupEntity.setIsNew(true);
        } catch (DuplicateKeyException e) {
            oldProductGroupEntity = queryOldProductGroupEntity(productGroupEntity);
            AssertUtil.notNull(oldProductGroupEntity, "创建商品组失败，请稍后重试");
            productGroupEntity.setId(oldProductGroupEntity.getId());
        }
    }


    private ProductGroupEntity queryOldProductGroupEntity(ProductGroupEntity productGroupEntity) {
        ProductGroupConditionEntity productConditionEntity = new ProductGroupConditionEntity();
        productConditionEntity.setCategoryId(productGroupEntity.getCategoryId());
        productConditionEntity.setUnitId(productGroupEntity.getUnitId());
        productConditionEntity.setHash(productGroupEntity.getHash());
        productConditionEntity.setPageSize(1);
        List<ProductGroupEntity> productGroupEntities = productGroupMapper.searchByCondition(productConditionEntity);
        if (CollectionUtils.isNotEmpty(productGroupEntities)) {
            return productGroupEntities.get(0);
        }
        return null;
    }
}
