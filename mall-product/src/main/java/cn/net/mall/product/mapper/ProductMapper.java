package cn.net.mall.product.mapper;

import cn.net.mall.product.entity.ProductConditionEntity;
import cn.net.mall.product.entity.ProductEntity;
import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品 mapper
 *
 * @date 2024-05-09 14:43:56
 */
public interface ProductMapper extends BaseMapper<ProductEntity, ProductConditionEntity> {
    ProductEntity findById(Long id);

    int insert(ProductEntity productEntity);

    int batchInsert(List<ProductEntity> productEntities);

    int update(ProductEntity productEntity);

    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") ProductEntity entity);

    List<ProductEntity> findByIds(List<Long> ids);

    List<ProductEntity> getTopProducts(@Param("limit") int limit);
}
