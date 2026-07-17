package cn.net.mall.inventory.mapper;

import cn.net.mall.inventory.entity.InventoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库存 Mapper.
 */
@Mapper
public interface InventoryMapper {

    List<InventoryEntity> findAll();

    InventoryEntity findByProductId(@Param("productId") Long productId);

    int updateById(InventoryEntity entity);

    int insert(InventoryEntity entity);

    /**
     * 乐观锁扣减可用库存（带版本号，Redis 主路径使用）.
     */
    int decrAvailable(@Param("productId") Long productId,
                      @Param("quantity") int quantity,
                      @Param("version") int version);

    /**
     * 条件扣减可用库存（不带版本号，Redis 降级路径使用）.
     * MySQL 行锁保证原子性，不会因版本冲突误报库存不足。
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
