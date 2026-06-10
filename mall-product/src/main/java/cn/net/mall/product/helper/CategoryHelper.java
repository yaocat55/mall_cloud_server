package cn.net.mall.product.helper;

import cn.net.mall.product.entity.BaseProductEntity;
import cn.net.mall.product.entity.CategoryConditionEntity;
import cn.net.mall.product.entity.CategoryEntity;
import cn.net.mall.product.mapper.CategoryMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类 helper
 *
 * @date 2024/9/8 下午1:40
 */
@Component
public class CategoryHelper {

    private final CategoryMapper categoryMapper;

    public CategoryHelper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * 填充分类信息
     *
     * @param list 商品信息
     */
    public void fillCategory(List<? extends BaseProductEntity> list) {
        List<Long> categorySysNoList = list.stream().map(BaseProductEntity::getCategoryId).distinct().collect(Collectors.toList());
        CategoryConditionEntity categoryConditionEntity = new CategoryConditionEntity();
        categoryConditionEntity.setIdList(categorySysNoList);
        List<CategoryEntity> categoryEntities = categoryMapper.searchByCondition(categoryConditionEntity);
        if (CollectionUtils.isEmpty(categoryEntities)) {
            return;
        }

        Map<Long, List<CategoryEntity>> categoryMap = categoryEntities.stream().collect(Collectors.groupingBy(CategoryEntity::getId));
        for (BaseProductEntity baseProductEntity : list) {
            List<CategoryEntity> findCategoryList = categoryMap.get(baseProductEntity.getCategoryId());
            if (CollectionUtils.isNotEmpty(findCategoryList)) {
                baseProductEntity.setCategoryName(findCategoryList.get(0).getName());
            }
        }
    }
}
