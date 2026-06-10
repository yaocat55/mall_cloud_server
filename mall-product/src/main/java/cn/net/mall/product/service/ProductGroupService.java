package cn.net.mall.product.service;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.entity.ProductConditionEntity;
import cn.net.mall.product.entity.ProductEntity;
import cn.net.mall.product.entity.ProductGroupConditionEntity;
import cn.net.mall.product.entity.ProductGroupEntity;
import cn.net.mall.product.helper.CategoryHelper;
import cn.net.mall.product.helper.UnitHelper;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.product.mapper.ProductGroupMapper;
import cn.net.mall.product.mapper.ProductMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品组 服务层
 *
 * @date 2024-09-07 17:28:47
 */
@Service
public class ProductGroupService extends BaseService<ProductGroupEntity, ProductGroupConditionEntity> {

    private final ProductGroupMapper productGroupMapper;
    private final CategoryHelper categoryHelper;
    private final UnitHelper unitHelper;
    private final ProductMapper productMapper;

    public ProductGroupService(ProductGroupMapper productGroupMapper, CategoryHelper categoryHelper, UnitHelper unitHelper, ProductMapper productMapper) {
        this.productGroupMapper = productGroupMapper;
        this.categoryHelper = categoryHelper;
        this.unitHelper = unitHelper;
        this.productMapper = productMapper;
    }

    /**
     * 查询商品组信息
     *
     * @param id 商品组ID
     * @return 商品组信息
     */
    public ProductGroupEntity findById(Long id) {
        return productGroupMapper.findById(id);
    }

    /**
     * 根据条件分页查询商品组列表
     *
     * @param productGroupConditionEntity 商品组信息
     * @return 商品组集合
     */
    public ResponsePageEntity<ProductGroupEntity> searchByPage(ProductGroupConditionEntity productGroupConditionEntity) {
        ResponsePageEntity<ProductGroupEntity> responsePageEntity = super.searchByPage(productGroupConditionEntity);
        if (CollectionUtils.isNotEmpty(responsePageEntity.getData())) {
            categoryHelper.fillCategory(responsePageEntity.getData());
            unitHelper.fillUnit(responsePageEntity.getData());
        }
        return responsePageEntity;
    }

    /**
     * 新增商品组
     *
     * @param productGroupEntity 商品组信息
     * @return 结果
     */
    public int insert(ProductGroupEntity productGroupEntity) {
        return productGroupMapper.insert(productGroupEntity);
    }

    /**
     * 修改商品组
     *
     * @param productGroupEntity 商品组信息
     * @return 结果
     */
    public int update(ProductGroupEntity productGroupEntity) {
        return productGroupMapper.update(productGroupEntity);
    }

    /**
     * 批量删除商品组
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<ProductGroupEntity> entities = productGroupMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "商品组已被删除");

        ProductConditionEntity productConditionEntity = new ProductConditionEntity();
        productConditionEntity.setProductGroupIdList(ids);
        List<ProductEntity> productEntities = productMapper.searchByCondition(productConditionEntity);
        AssertUtil.isTrue(CollectionUtils.isEmpty(productEntities), "该商品组下存在商品，请先删除商品");

        ProductGroupEntity entity = new ProductGroupEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return productGroupMapper.deleteByIds(ids, entity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return productGroupMapper;
    }
}
