package cn.net.mall.inventory.service;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.inventory.entity.WarehouseConditionEntity;
import cn.net.mall.inventory.entity.WarehouseEntity;
import cn.net.mall.inventory.mapper.WarehouseMapper;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 仓库 服务层
 */
@Service
public class WarehouseService extends BaseService<WarehouseEntity, WarehouseConditionEntity> {

    @Autowired
    private WarehouseMapper warehouseMapper;

    /**
     * 查询仓库信息
     */
    public WarehouseEntity findById(Long id) {
        return warehouseMapper.findById(id);
    }

    /**
     * 根据条件分页查询仓库列表
     */
    public ResponsePageEntity<WarehouseEntity> searchByPage(WarehouseConditionEntity condition) {
        return super.searchByPage(condition);
    }

    /**
     * 新增仓库
     */
    public int insert(WarehouseEntity entity) {
        return warehouseMapper.insert(entity);
    }

    /**
     * 修改仓库
     */
    public int update(WarehouseEntity entity) {
        return warehouseMapper.update(entity);
    }

    /**
     * 批量删除仓库
     */
    public int deleteByIds(List<Long> ids) {
    	AssertUtil.notEmpty(ids, "请选择要删除的仓库");
        List<WarehouseEntity> entities = warehouseMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "仓库已被删除");

        WarehouseEntity entity = new WarehouseEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return warehouseMapper.deleteByIds(ids, entity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return warehouseMapper;
    }
}
