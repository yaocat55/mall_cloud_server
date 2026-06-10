package cn.net.mall.product.mapper;

import cn.net.mall.product.entity.ProductGroupAttributeConditionEntity;
import cn.net.mall.product.entity.ProductGroupAttributeEntity;
import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品组属性 mapper
 *
 * @date 2024-09-07 17:28:48
 */
public interface ProductGroupAttributeMapper extends BaseMapper<ProductGroupAttributeEntity, ProductGroupAttributeConditionEntity> {
    /**
     * 查询商品组属性信息
     *
     * @param id 商品组属性ID
     * @return 商品组属性信息
     */
    ProductGroupAttributeEntity findById(Long id);

    /**
     * 添加商品组属性
     *
     * @param productGroupAttributeEntity 商品组属性信息
     * @return 结果
     */
    int insert(ProductGroupAttributeEntity productGroupAttributeEntity);

    /**
     * 修改商品组属性
     *
     * @param productGroupAttributeEntity 商品组属性信息
     * @return 结果
     */
    int update(ProductGroupAttributeEntity productGroupAttributeEntity);

    /**
     * 批量删除商品组属性
     *
     * @param ids    id集合
     * @param entity 商品组属性实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") ProductGroupAttributeEntity entity);

    /**
     * 批量查询商品组属性信息
     *
     * @param ids ID集合
     * @return 商品组属性信息
     */
    List<ProductGroupAttributeEntity> findByIds(List<Long> ids);

    /**
     * 批量添加商品组属性
     *
     * @param list 商品组属性
     * @return 结果
     */
    int batchInsert(List<ProductGroupAttributeEntity> list);
}
