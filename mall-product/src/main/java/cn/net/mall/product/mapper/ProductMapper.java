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
    /**
     * 查询商品信息
     *
     * @param id 商品ID
     * @return 商品信息
     */
    ProductEntity findById(Long id);

    /**
     * 添加商品
     *
     * @param productEntity 商品信息
     * @return 结果
     */
    int insert(ProductEntity productEntity);

    /**
     * 批量添加商品
     *
     * @param productEntities 商品信息
     * @return 结果
     */
    int batchInsert(List<ProductEntity> productEntities);

    /**
     * 修改商品
     *
     * @param productEntity 商品信息
     * @return 结果
     */
    int update(ProductEntity productEntity);

    /**
     * 批量删除商品
     *
     * @param ids    id集合
     * @param entity 商品实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") ProductEntity entity);

    /**
     * 批量查询商品信息
     *
     * @param ids ID集合
     * @return 部门信息
     */
    List<ProductEntity> findByIds(List<Long> ids);

    /**
     * 扣减库存
     *
     * @param id       主键
     * @param quantity 商品数量
     * @return 结果
     */
    int reduceStock(@Param("id") Long id, int quantity);

    /**
     * 回滚库存（补偿用）
     *
     * @param id       主键
     * @param quantity 商品数量
     * @return 结果
     */
    int addStock(@Param("id") Long id, @Param("quantity") int quantity);
}
