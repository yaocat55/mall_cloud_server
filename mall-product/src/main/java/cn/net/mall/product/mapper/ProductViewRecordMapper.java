package cn.net.mall.product.mapper;

import cn.net.mall.product.entity.ProductViewRecordConditionEntity;
import cn.net.mall.product.entity.ProductViewRecordEntity;
import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品浏览记录 mapper
 *
 * @date 2024-09-04 15:12:10
 */
public interface ProductViewRecordMapper extends BaseMapper<ProductViewRecordEntity, ProductViewRecordConditionEntity> {
	/**
     * 查询商品浏览记录信息
     *
     * @param id 商品浏览记录ID
     * @return 商品浏览记录信息
     */
	ProductViewRecordEntity findById(Long id);

	/**
     * 添加商品浏览记录
     *
     * @param productViewRecordEntity 商品浏览记录信息
     * @return 结果
     */
	int insert(ProductViewRecordEntity productViewRecordEntity);

	/**
     * 修改商品浏览记录
     *
     * @param productViewRecordEntity 商品浏览记录信息
     * @return 结果
     */
	int update(ProductViewRecordEntity productViewRecordEntity);

    /**
     * 批量删除商品浏览记录
     *
     * @param ids id集合
     * @param entity 商品浏览记录实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") ProductViewRecordEntity entity);

    /**
     * 批量查询商品浏览记录信息
     *
     * @param ids ID集合
     * @return 商品浏览记录信息
    */
    List<ProductViewRecordEntity> findByIds(List<Long> ids);
}
