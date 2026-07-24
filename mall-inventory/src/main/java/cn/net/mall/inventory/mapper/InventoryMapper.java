package cn.net.mall.inventory.mapper;

import cn.net.mall.inventory.entity.InventoryConditionEntity;
import cn.net.mall.inventory.entity.InventoryEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库存 Mapper.
 */
public interface InventoryMapper extends BaseMapper<InventoryEntity, InventoryConditionEntity> {

    List<InventoryEntity> findAll();

    InventoryEntity findByProductId(@Param("productId") Long productId);

    int updateById(InventoryEntity entity);

    int insert(InventoryEntity entity);

    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") InventoryEntity entity);

    List<InventoryEntity> findByIds(List<Long> ids);

    /**
     * 乐观锁扣减可用库存（带版本号，Redis 主路径使用）.
     */
    int decrAvailable(@Param("productId") Long productId,
                      @Param("quantity") int quantity,
                      @Param("version") int version);

    /**
     * 条件扣减可用库存（不带版本号，Redis 降级路径使用）.
     */
    int decrAvailableFallback(@Param("productId") Long productId,
                              @Param("quantity") int quantity);

    int incrAvailable(@Param("productId") Long productId,
                      @Param("quantity") int quantity);

    int incrFrozen(@Param("productId") Long productId,
                   @Param("quantity") int quantity);

    int decrFrozen(@Param("productId") Long productId,
                   @Param("quantity") int quantity);

    int incrQuantity(@Param("productId") Long productId,
                     @Param("quantity") int quantity);

    int incrSaleCount(@Param("productId") Long productId,
                      @Param("quantity") int quantity);
}
