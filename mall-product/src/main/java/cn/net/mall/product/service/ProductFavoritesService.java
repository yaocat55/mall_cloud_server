package cn.net.mall.product.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.product.entity.ProductFavoritesConditionEntity;
import cn.net.mall.product.entity.ProductFavoritesEntity;
import cn.net.mall.product.helper.UserProductHelper;
import cn.net.mall.product.mapper.ProductFavoritesMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品收藏 服务层
 *
 * @date 2024-09-04 15:12:10
 */
@AllArgsConstructor
@Service
public class ProductFavoritesService extends BaseService<ProductFavoritesEntity, ProductFavoritesConditionEntity> {

    private final ProductFavoritesMapper productFavoritesMapper;
    private final UserProductHelper userProductHelper;

    /**
     * 查询商品收藏信息
     *
     * @param id 商品收藏ID
     * @return 商品收藏信息
     */
    public ProductFavoritesEntity findById(Long id) {
        return productFavoritesMapper.findById(id);
    }

    /**
     * 根据条件分页查询商品收藏列表
     *
     * @param productFavoritesConditionEntity 商品收藏信息
     * @return 商品收藏集合
     */
    public ResponsePageEntity<ProductFavoritesEntity> searchByPage(ProductFavoritesConditionEntity productFavoritesConditionEntity) {
        ResponsePageEntity<ProductFavoritesEntity> responsePageEntity = super.searchByPage(productFavoritesConditionEntity);
        return responsePageEntity;
    }

    public Boolean addOrCancelFavorites(ProductFavoritesEntity productFavoritesEntity) {
        userProductHelper.checkParam(productFavoritesEntity);

        ProductFavoritesConditionEntity productFavoritesConditionEntity = new ProductFavoritesConditionEntity();
        productFavoritesConditionEntity.setUserId(productFavoritesEntity.getUserId());
        productFavoritesConditionEntity.setProductId(productFavoritesEntity.getProductId());
        List<ProductFavoritesEntity> productFavoritesEntities = productFavoritesMapper.searchByCondition(productFavoritesConditionEntity);
        if (CollectionUtil.isNotEmpty(productFavoritesEntities)) {
            ProductFavoritesEntity oldProductFavoritesEntity = productFavoritesEntities.get(0);
            if (oldProductFavoritesEntity.getIsDel() == 1) {
                oldProductFavoritesEntity.setIsDel(0);
                FillUserUtil.fillUpdateUserInfo(oldProductFavoritesEntity);
                productFavoritesMapper.update(oldProductFavoritesEntity);
                return Boolean.TRUE;
            } else {
                ProductFavoritesEntity entity = new ProductFavoritesEntity();
                FillUserUtil.fillUpdateUserInfo(entity);
                productFavoritesMapper.deleteByIds(Lists.newArrayList(oldProductFavoritesEntity.getId()), entity);
                return Boolean.FALSE;
            }
        }

        productFavoritesMapper.insert(productFavoritesEntity);
        return Boolean.TRUE;
    }

    /**
     * 新增商品收藏
     *
     * @param productFavoritesEntity 商品收藏信息
     * @return 结果
     */
    public void insert(ProductFavoritesEntity productFavoritesEntity) {
        userProductHelper.checkParam(productFavoritesEntity);

        ProductFavoritesConditionEntity productFavoritesConditionEntity = new ProductFavoritesConditionEntity();
        productFavoritesConditionEntity.setUserId(productFavoritesEntity.getUserId());
        productFavoritesConditionEntity.setProductId(productFavoritesEntity.getProductId());
        List<ProductFavoritesEntity> productFavoritesEntities = productFavoritesMapper.searchByCondition(productFavoritesConditionEntity);
        if (CollectionUtil.isNotEmpty(productFavoritesEntities)) {
            return;
        }

        productFavoritesMapper.insert(productFavoritesEntity);
    }

    /**
     * 修改商品收藏
     *
     * @param productFavoritesEntity 商品收藏信息
     * @return 结果
     */
    public int update(ProductFavoritesEntity productFavoritesEntity) {
        userProductHelper.checkParam(productFavoritesEntity);
        FillUserUtil.fillUpdateUserInfo(productFavoritesEntity);
        return productFavoritesMapper.update(productFavoritesEntity);
    }

    /**
     * 批量删除商品收藏
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<ProductFavoritesEntity> entities = productFavoritesMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "商品收藏已被删除");

        ProductFavoritesEntity entity = new ProductFavoritesEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return productFavoritesMapper.deleteByIds(ids, entity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return productFavoritesMapper;
    }
}
