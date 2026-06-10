package cn.net.mall.product.service;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.product.entity.AttributeConditionEntity;
import cn.net.mall.product.entity.AttributeEntity;
import cn.net.mall.product.entity.AttributeValueConditionEntity;
import cn.net.mall.product.entity.AttributeValueEntity;
import cn.net.mall.product.mapper.AttributeMapper;
import cn.net.mall.product.mapper.AttributeValueMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 属性值 服务层
 *
 * @date 2024-05-09 14:43:55
 */
@Service
public class AttributeValueService extends BaseService<AttributeValueEntity, AttributeValueConditionEntity> {

    private final AttributeValueMapper attributeValueMapper;
    private final AttributeMapper attributeMapper;

    public AttributeValueService(AttributeValueMapper attributeValueMapper, AttributeMapper attributeMapper) {
        this.attributeValueMapper = attributeValueMapper;
        this.attributeMapper = attributeMapper;
    }

    /**
     * 查询属性值信息
     *
     * @param id 属性值ID
     * @return 属性值信息
     */
    public AttributeValueEntity findById(Long id) {
        return attributeValueMapper.findById(id);
    }

    /**
     * 根据条件分页查询属性值列表
     *
     * @param attributeValueConditionEntity 属性值信息
     * @return 属性值集合
     */
    public ResponsePageEntity<AttributeValueEntity> searchByPage(AttributeValueConditionEntity attributeValueConditionEntity) {
        ResponsePageEntity<AttributeValueEntity> attributeValueEntityResponsePageEntity = super.searchByPage(attributeValueConditionEntity);
        if (CollectionUtils.isNotEmpty(attributeValueEntityResponsePageEntity.getData())) {
            fillAttribute(attributeValueEntityResponsePageEntity.getData());
        }
        return attributeValueEntityResponsePageEntity;
    }


    private void fillAttribute(List<AttributeValueEntity> list) {
        List<Long> attributeSysNoList = list.stream().map(AttributeValueEntity::getAttributeId).distinct().collect(Collectors.toList());
        AttributeConditionEntity attributeConditionEntity = new AttributeConditionEntity();
        attributeConditionEntity.setIdList(attributeSysNoList);
        List<AttributeEntity> attributeEntities = attributeMapper.searchByCondition(attributeConditionEntity);
        if (CollectionUtils.isEmpty(attributeEntities)) {
            return;
        }

        Map<Long, List<AttributeEntity>> attributeMap = attributeEntities.stream().collect(Collectors.groupingBy(AttributeEntity::getId));
        for (AttributeValueEntity attributeValueEntity : list) {
            List<AttributeEntity> attributeEntityList = attributeMap.get(attributeValueEntity.getAttributeId());
            if (CollectionUtils.isNotEmpty(attributeEntityList)) {
                AttributeEntity attributeEntity = attributeEntityList.get(0);
                if (Objects.nonNull(attributeEntity)) {
                    attributeValueEntity.setAttributeName(attributeEntity.getName());
                }
            }

        }
    }

    /**
     * 新增属性值
     *
     * @param attributeValueEntity 属性值信息
     * @return 结果
     */
    public int insert(AttributeValueEntity attributeValueEntity) {
        checkParam(attributeValueEntity);
        return attributeValueMapper.insert(attributeValueEntity);
    }

    /**
     * 修改属性值
     *
     * @param attributeValueEntity 属性值信息
     * @return 结果
     */
    public int update(AttributeValueEntity attributeValueEntity) {
        AssertUtil.notNull(attributeValueEntity.getId(), "id不能为空");
        checkParam(attributeValueEntity);
        return attributeValueMapper.update(attributeValueEntity);
    }

    private void checkParam(AttributeValueEntity attributeValueEntity) {
        attributeValueEntity.setValue(attributeValueEntity.getValue().trim());

        AttributeValueConditionEntity attributeValueConditionEntity = new AttributeValueConditionEntity();
        attributeValueConditionEntity.setAttributeId(attributeValueEntity.getAttributeId());
        attributeValueConditionEntity.setValue(attributeValueEntity.getValue().trim());
        List<AttributeValueEntity> attributeValueEntities = attributeValueMapper.searchByCondition(attributeValueConditionEntity);
        if (Objects.nonNull(attributeValueEntity.getId())) {
            Optional<AttributeValueEntity> optional = attributeValueEntities.stream()
                    .filter(x -> !x.getId().equals(attributeValueEntity.getId())).findAny();
            if (optional.isPresent()) {
                throw new BusinessException("该属性值已存在");
            }
        } else {
            if (CollectionUtils.isNotEmpty(attributeValueEntities)) {
                throw new BusinessException("该属性值已存在");
            }
        }
    }

    /**
     * 批量删除属性值对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<AttributeValueEntity> entities = attributeValueMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "属性值已被删除");

        AttributeValueEntity entity = new AttributeValueEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return attributeValueMapper.deleteByIds(ids, entity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return attributeValueMapper;
    }

}
