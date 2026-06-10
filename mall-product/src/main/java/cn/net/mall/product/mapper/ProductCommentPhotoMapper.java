package cn.net.mall.product.mapper;

import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.product.entity.ProductCommentPhotoConditionEntity;
import cn.net.mall.product.entity.ProductCommentPhotoEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductCommentPhotoMapper extends BaseMapper<ProductCommentPhotoEntity, ProductCommentPhotoConditionEntity> {
    ProductCommentPhotoEntity findById(Long id);
    int insert(ProductCommentPhotoEntity entity);
    int batchInsert(List<ProductCommentPhotoEntity> list);
    int update(ProductCommentPhotoEntity entity);
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") ProductCommentPhotoEntity entity);
    List<ProductCommentPhotoEntity> findByIds(List<Long> ids);
}
