package cn.net.mall.product.helper;

import cn.net.mall.product.entity.BaseProductEntity;
import cn.net.mall.product.entity.UnitConditionEntity;
import cn.net.mall.product.entity.UnitEntity;
import cn.net.mall.product.mapper.UnitMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 单位helper
 *
 * @date 2024/9/8 下午1:41
 */
@Component
public class UnitHelper {

    private final UnitMapper unitMapper;

    public UnitHelper(UnitMapper unitMapper) {
        this.unitMapper = unitMapper;
    }


    /**
     * 添加单位信息
     *
     * @param list 商品信息
     */
    public void fillUnit(List<? extends BaseProductEntity> list) {
        List<Long> unitSysNoList = list.stream().map(BaseProductEntity::getUnitId).distinct().collect(Collectors.toList());
        UnitConditionEntity unitConditionEntity = new UnitConditionEntity();
        unitConditionEntity.setIdList(unitSysNoList);
        List<UnitEntity> unitEntities = unitMapper.searchByCondition(unitConditionEntity);
        if (CollectionUtils.isEmpty(unitEntities)) {
            return;
        }

        Map<Long, List<UnitEntity>> unitMap = unitEntities.stream().collect(Collectors.groupingBy(UnitEntity::getId));
        for (BaseProductEntity baseProductEntity : list) {
            List<UnitEntity> findList = unitMap.get(baseProductEntity.getUnitId());
            if (CollectionUtils.isEmpty(findList)) {
                continue;
            }
            baseProductEntity.setUnitName(findList.get(0).getName());
        }
    }
}
