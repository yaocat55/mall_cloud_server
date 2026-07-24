package cn.net.mall.inventory.service;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.inventory.entity.InventoryBatchConditionEntity;
import cn.net.mall.inventory.entity.InventoryBatchEntity;
import cn.net.mall.inventory.mapper.InventoryBatchMapper;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 库存批次 服务层
 */
@Service
public class InventoryBatchService extends BaseService<InventoryBatchEntity, InventoryBatchConditionEntity> {

    @Autowired
    private InventoryBatchMapper inventoryBatchMapper;

    /**
     * 根据条件分页查询批次列表
     */
    public ResponsePageEntity<InventoryBatchEntity> searchByPage(InventoryBatchConditionEntity condition) {
        return super.searchByPage(condition);
    }

    /**
     * 查询批次详情
     */
    public InventoryBatchEntity findById(Long id) {
        return inventoryBatchMapper.findById(id);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return inventoryBatchMapper;
    }
}
