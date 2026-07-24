package cn.net.mall.inventory.mapper;

import cn.net.mall.inventory.entity.WarehouseConditionEntity;
import cn.net.mall.inventory.entity.WarehouseEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 仓库 mapper
 */
public interface WarehouseMapper extends BaseMapper<WarehouseEntity, WarehouseConditionEntity> {
    /**
     * 查询仓库信息
     */
    WarehouseEntity findById(Long id);

    /**
     * 批量查询仓库信息
     */
    List<WarehouseEntity> findByIds(List<Long> ids);

    /**
     * 添加仓库
     */
    int insert(WarehouseEntity entity);

    /**
     * 修改仓库
     */
    int update(WarehouseEntity entity);

    /**
     * 批量删除仓库
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") WarehouseEntity entity);
}
