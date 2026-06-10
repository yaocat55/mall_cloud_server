package cn.net.mall.product.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.net.mall.product.dto.CategoryTreeDTO;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.entity.CategoryConditionEntity;
import cn.net.mall.product.entity.CategoryEntity;
import cn.net.mall.product.entity.web.CategoryWebEntity;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.product.mapper.CategoryMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 分类 服务层
 *
 * @date 2024-05-09 14:43:55
 */
@Service
public class CategoryService extends BaseService<CategoryEntity, CategoryConditionEntity> {

    private final CategoryMapper categoryMapper;
    private final TransactionTemplate transactionTemplate;

    public CategoryService(CategoryMapper categoryMapper, TransactionTemplate transactionTemplate) {
        this.categoryMapper = categoryMapper;
        this.transactionTemplate = transactionTemplate;
    }

    /**
     * 根据父分类ID查询分类列表
     *
     * @param parentId 父分类ID
     * @return 分类列表
     */
    public List<CategoryEntity> getCategoryByParentId(Long parentId) {
        CategoryConditionEntity categoryConditionEntity = new CategoryConditionEntity();
        categoryConditionEntity.setParentId(parentId);
        return categoryMapper.searchByCondition(categoryConditionEntity);
    }

    /**
     * 查询分类信息
     *
     * @param id 分类ID
     * @return 分类信息
     */
    public CategoryEntity findById(Long id) {
        return categoryMapper.findById(id);
    }

    public List<CategoryTreeDTO> searchByTree(CategoryConditionEntity categoryConditionEntity) {
        if (Objects.isNull(categoryConditionEntity.getParentId())) {
            categoryConditionEntity.setParentId(0L);
        }
        List<CategoryEntity> dataList = categoryMapper.searchByCondition(categoryConditionEntity);
        return buildDeptTree(dataList, categoryConditionEntity.getQueryTree());
    }

    private List<CategoryTreeDTO> buildDeptTree(List<CategoryEntity> dataList, Boolean queryTree) {
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        List<CategoryTreeDTO> categoryTreeDTOList = dataList.stream().map(x -> convertToDeptTreeDTO(x)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(categoryTreeDTOList)) {
            return Collections.emptyList();
        }

        for (CategoryTreeDTO categoryTreeDTO : categoryTreeDTOList) {
            buildChildren(categoryTreeDTO, queryTree);
        }

        return categoryTreeDTOList;
    }

    private CategoryTreeDTO convertToDeptTreeDTO(CategoryEntity deptEntity) {
        CategoryTreeDTO categoryTreeDTO = new CategoryTreeDTO();
        categoryTreeDTO.setId(deptEntity.getId());
        categoryTreeDTO.setName(deptEntity.getName());
        categoryTreeDTO.setLabel(deptEntity.getName());
        categoryTreeDTO.setPid(deptEntity.getParentId());
        categoryTreeDTO.setCreateTime(deptEntity.getCreateTime());
        return categoryTreeDTO;
    }

    private void buildChildren(CategoryTreeDTO categoryTreeDTO, Boolean queryTree) {
        categoryTreeDTO.setLeaf(false);
        CategoryConditionEntity deptConditionEntity = new CategoryConditionEntity();
        deptConditionEntity.setParentId(categoryTreeDTO.getId());
        deptConditionEntity.setPageNo(0);
        List<CategoryEntity> categoryEntities = categoryMapper.searchByCondition(deptConditionEntity);
        if (CollectionUtils.isNotEmpty(categoryEntities)) {
            if (BooleanUtil.isTrue(queryTree)) {
                for (CategoryEntity categoryEntity : categoryEntities) {
                    CategoryTreeDTO childDeptTreeDTO = convertToDeptTreeDTO(categoryEntity);
                    categoryTreeDTO.addChildren(childDeptTreeDTO);
                    buildChildren(childDeptTreeDTO, queryTree);
                }
            }
        } else {
            categoryTreeDTO.setLeaf(true);
        }
        categoryTreeDTO.setSubCount(CollectionUtils.isEmpty(categoryEntities) ? 0 : categoryEntities.size());
        categoryTreeDTO.setHasChildren(!categoryTreeDTO.getLeaf());
    }

    /**
     * 根据条件分页查询分类列表
     *
     * @param categoryConditionEntity 分类信息
     * @return 分类集合
     */
    public ResponsePageEntity<CategoryEntity> searchByPage(CategoryConditionEntity categoryConditionEntity) {
        return super.searchByPage(categoryConditionEntity);
    }

    /**
     * 新增分类
     *
     * @param categoryEntity 分类信息
     * @return 结果
     */
    public void insert(CategoryEntity categoryEntity) {
        checkParam(categoryEntity);
        if (categoryEntity.getParentId() == 0) {
            categoryEntity.setLevel(1);
            categoryEntity.setIsLeaf(1);
            categoryMapper.insert(categoryEntity);
        } else {
            CategoryConditionEntity categoryConditionEntity = new CategoryConditionEntity();
            categoryConditionEntity.setId(categoryEntity.getParentId());
            List<CategoryEntity> categoryEntities = categoryMapper.searchByCondition(categoryConditionEntity);
            AssertUtil.notEmpty(categoryEntities, "父分类ID在系统中不存在");

            CategoryEntity parentCategoryEntity = categoryEntities.get(0);
            parentCategoryEntity.setIsLeaf(0);

            categoryEntity.setLevel(parentCategoryEntity.getLevel() + 1);
            categoryEntity.setIsLeaf(1);

            transactionTemplate.execute((status -> {
                categoryMapper.update(parentCategoryEntity);
                categoryMapper.insert(categoryEntity);
                return Boolean.TRUE;
            }));
        }
    }

    private void checkParam(CategoryEntity categoryEntity) {
        categoryEntity.setName(categoryEntity.getName().trim());

        CategoryConditionEntity categoryConditionEntity = new CategoryConditionEntity();
        categoryConditionEntity.setParentId(categoryEntity.getParentId());
        categoryConditionEntity.setName(categoryEntity.getName());
        List<CategoryEntity> categoryEntities = categoryMapper.searchByCondition(categoryConditionEntity);
        if (Objects.nonNull(categoryEntity.getId())) {
            Optional<CategoryEntity> optional = categoryEntities.stream()
                    .filter(x -> !x.getId().equals(categoryEntity.getId())).findAny();
            if (optional.isPresent()) {
                throw new BusinessException("该分类名称在系统中已存在");
            }
        } else {
            if (CollectionUtils.isNotEmpty(categoryEntities)) {
                throw new BusinessException("该分类名称在系统中已存在");
            }
        }
    }

    /**
     * 修改分类
     *
     * @param categoryEntity 分类信息
     * @return 结果
     */
    public int update(CategoryEntity categoryEntity) {
        AssertUtil.notNull(categoryEntity.getId(), "id不能为空");
        checkParam(categoryEntity);
        return categoryMapper.update(categoryEntity);
    }

    /**
     * 批量删除分类对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<CategoryEntity> entities = categoryMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "分类已被删除");

        CategoryEntity entity = new CategoryEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return categoryMapper.deleteByIds(ids, entity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return categoryMapper;
    }

}
