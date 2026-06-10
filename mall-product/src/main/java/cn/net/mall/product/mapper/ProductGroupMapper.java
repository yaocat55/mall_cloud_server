package cn.net.mall.product.mapper;

import cn.net.mall.product.entity.ProductGroupConditionEntity;
import cn.net.mall.product.entity.ProductGroupEntity;
import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品组 mapper
 *
 * @date 2024-09-07 17:28:47
 */
public interface ProductGroupMapper extends BaseMapper<ProductGroupEntity, ProductGroupConditionEntity> {
    /**
     * 查询商品组信息
     *
     * @param id 商品组ID
     * @return 商品组信息
     */
    ProductGroupEntity findById(Long id);

    /**
     * 添加商品组
     *
     * @param productGroupEntity 商品组信息
     * @return 结果
     */
    int insert(ProductGroupEntity productGroupEntity);

    /**
     * 修改商品组
     *
     * @param productGroupEntity 商品组信息
     * @return 结果
     */
    int update(ProductGroupEntity productGroupEntity);

    /**
     * 批量删除商品组
     *
     * @param ids    id集合
     * @param entity 商品组实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") ProductGroupEntity entity);

    /**
     * 批量查询商品组信息
     *
     * @param ids ID集合
     * @return 商品组信息
     */
    List<ProductGroupEntity> findByIds(List<Long> ids);

    /**
     * 批量添加商品组
     *
     * @param list 商品组集合
     * @return 结果
     */
    int batchInsert(List<ProductGroupEntity> list);
}
