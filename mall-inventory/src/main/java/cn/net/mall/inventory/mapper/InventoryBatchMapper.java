package cn.net.mall.inventory.mapper;

import cn.net.mall.inventory.entity.InventoryBatchEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库存批次 Mapper.
 */
@Mapper
public interface InventoryBatchMapper {

    int insert(InventoryBatchEntity entity);

    int updateById(InventoryBatchEntity entity);

    /**
     * FIFO 查询：按入库时间升序取可用批次.
     */
    List<InventoryBatchEntity> findAvailableByProductId(@Param("productId") Long productId);

    /**
     * 逐批扣减剩余数量.
     */
    int decrAvailable(@Param("id") Long id,
                      @Param("quantity") int quantity,
                      @Param("version") int version);
}
