package cn.net.mall.inventory.mapper;

import cn.net.mall.inventory.entity.InventoryLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存流水 Mapper.
 */
@Mapper
public interface InventoryLogMapper {
    int insert(InventoryLogEntity entity);
}
