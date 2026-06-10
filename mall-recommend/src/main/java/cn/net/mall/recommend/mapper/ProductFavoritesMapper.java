package cn.net.mall.recommend.mapper;

import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.recommend.entity.ProductFavoritesConditionEntity;
import cn.net.mall.recommend.entity.ProductFavoritesEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductFavoritesMapper extends BaseMapper<ProductFavoritesEntity, ProductFavoritesConditionEntity> {
    ProductFavoritesEntity findById(Long id);
    int insert(ProductFavoritesEntity entity);
    int update(ProductFavoritesEntity entity);
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") ProductFavoritesEntity entity);
    List<ProductFavoritesEntity> findByIds(@Param("ids") List<Long> ids);
}
