package cn.net.mall.recommend.mapper;

import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.recommend.entity.ProductViewRecordConditionEntity;
import cn.net.mall.recommend.entity.ProductViewRecordEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductViewRecordMapper extends BaseMapper<ProductViewRecordEntity, ProductViewRecordConditionEntity> {
    ProductViewRecordEntity findById(Long id);
    int insert(ProductViewRecordEntity entity);
    int update(ProductViewRecordEntity entity);
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") ProductViewRecordEntity entity);
    List<ProductViewRecordEntity> findByIds(@Param("ids") List<Long> ids);
}
