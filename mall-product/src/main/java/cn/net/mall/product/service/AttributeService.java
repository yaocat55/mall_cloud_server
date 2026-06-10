package cn.net.mall.product.service;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.product.entity.AttributeConditionEntity;
import cn.net.mall.product.entity.AttributeEntity;
import cn.net.mall.product.mapper.AttributeMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 属性 服务层
 *
 * @date 2024-05-09 14:43:55
 */
@Service
public class AttributeService extends BaseService<AttributeEntity, AttributeConditionEntity> {


    private  final AttributeMapper attributeMapper;

    public AttributeService(AttributeMapper attributeMapper) {
        this.attributeMapper = attributeMapper;
    }

    /**
     * 查询属性信息
     *
     * @param id 属性ID
     * @return 属性信息
     */
    public AttributeEntity findById(Long id) {
        return attributeMapper.findById(id);
    }

    /**
     * 根据条件分页查询属性列表
     *
     * @param attributeConditionEntity 属性信息
     * @return 属性集合
     */
    public ResponsePageEntity<AttributeEntity> searchByPage(AttributeConditionEntity attributeConditionEntity) {
        return super.searchByPage(attributeConditionEntity);
    }

    /**
     * 新增属性
     *
     * @param attributeEntity 属性信息
     * @return 结果
     */
    public int insert(AttributeEntity attributeEntity) {
        checkParam(attributeEntity);
        return attributeMapper.insert(attributeEntity);
    }

    private void checkParam(AttributeEntity attributeEntity) {
		attributeEntity.setName(attributeEntity.getName().trim());

        AttributeConditionEntity attributeConditionEntity = new AttributeConditionEntity();
        attributeConditionEntity.setName(attributeEntity.getName());
        List<AttributeEntity> categoryEntities = attributeMapper.searchByCondition(attributeConditionEntity);

        if (Objects.nonNull(attributeEntity.getId())) {
            Optional<AttributeEntity> optional = categoryEntities.stream().filter(x -> !x.getId().equals(attributeEntity.getId())).findAny();
            if (optional.isPresent()) {
                throw new BusinessException("该属性在系统中已存在");
            }
        } else {
            if (CollectionUtils.isNotEmpty(categoryEntities)) {
                throw new BusinessException("该属性在系统中已存在");
            }
        }

    }

    /**
     * 修改属性
     *
     * @param attributeEntity 属性信息
     * @return 结果
     */
    public int update(AttributeEntity attributeEntity) {
        AssertUtil.notNull(attributeEntity.getId(), "id不能为空");
        checkParam(attributeEntity);
        return attributeMapper.update(attributeEntity);
    }

    /**
     * 批量删除属性对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<AttributeEntity> entities = attributeMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "属性已被删除");

        AttributeEntity entity = new AttributeEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return attributeMapper.deleteByIds(ids, entity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return attributeMapper;
    }

}
