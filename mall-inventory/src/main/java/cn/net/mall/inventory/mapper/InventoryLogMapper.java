package cn.net.mall.inventory.mapper;

import cn.net.mall.inventory.entity.InventoryLogConditionEntity;
import cn.net.mall.inventory.entity.InventoryLogEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库存流水 Mapper.
 */
public interface InventoryLogMapper extends BaseMapper<InventoryLogEntity, InventoryLogConditionEntity> {

    int insert(InventoryLogEntity entity);

    InventoryLogEntity findById(Long id);

    List<InventoryLogEntity> findByIds(List<Long> ids);
}
