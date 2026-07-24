package cn.net.mall.inventory.service;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.inventory.entity.InventoryLogConditionEntity;
import cn.net.mall.inventory.entity.InventoryLogEntity;
import cn.net.mall.inventory.mapper.InventoryLogMapper;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 库存流水 服务层
 */
@Service
public class InventoryLogService extends BaseService<InventoryLogEntity, InventoryLogConditionEntity> {

    @Autowired
    private InventoryLogMapper inventoryLogMapper;

    /**
     * 根据条件分页查询流水列表
     */
    public ResponsePageEntity<InventoryLogEntity> searchByPage(InventoryLogConditionEntity condition) {
        return super.searchByPage(condition);
    }

    /**
     * 查询流水详情
     */
    public InventoryLogEntity findById(Long id) {
        return inventoryLogMapper.findById(id);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return inventoryLogMapper;
    }
}
