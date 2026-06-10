package cn.net.mall.product.util;

import cn.net.mall.product.entity.AttributeValueEntity;
import cn.net.mall.util.Md5Util;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 属性工具
 *
 * @date 2024/5/9 下午3:37
 */
public abstract class AttributeUtil {

    private AttributeUtil() {

    }

    /**
     * 根据属性集合拼接名称
     *
     * @param attributeValueEntities 属性集合
     * @return 规格
     */
    public static String getName(List<AttributeValueEntity> attributeValueEntities) {
        List<AttributeValueEntity> sortAttributeValueEntities = attributeValueEntities.stream()
                .sorted((a, b) -> a.getAttributeName().compareTo(b.getAttributeName())).collect(Collectors.toList());

        StringBuilder modelBuilder = new StringBuilder();
        for (AttributeValueEntity attributeValueEntity : sortAttributeValueEntities) {
            modelBuilder.append(attributeValueEntity.getValue()).append(" ");
        }
        modelBuilder.deleteCharAt(modelBuilder.length() - 1);
        return modelBuilder.toString();
    }

    /**
     * 根据属性集合拼接规格
     *
     * @param attributeValueEntities 属性集合
     * @return 规格
     */
    public static String getModel(List<AttributeValueEntity> attributeValueEntities) {
        StringBuilder modelBuilder = new StringBuilder();
        for (AttributeValueEntity attributeValueEntity : attributeValueEntities) {
            modelBuilder.append(attributeValueEntity.getAttributeName())
                    .append(":")
                    .append(attributeValueEntity.getValue())
                    .append(" ");
        }
        modelBuilder.deleteCharAt(modelBuilder.length() - 1);
        return modelBuilder.toString();
    }

    /**
     * 根据属性集合排序之后拼接规格
     *
     * @param attributeValueEntities 属性集合
     * @return 规格
     */
    public static String getSortModel(List<AttributeValueEntity> attributeValueEntities) {
        List<AttributeValueEntity> sortAttributeValueEntities = attributeValueEntities.stream()
                .sorted((a, b) -> a.getAttributeName().compareTo(b.getAttributeName())).collect(Collectors.toList());
        return getModel(sortAttributeValueEntities);
    }

    /**
     * 获取model的hash值
     *
     * @param attributeValueEntities 属性集合
     * @return hash值
     */
    public static String getModelHash(List<AttributeValueEntity> attributeValueEntities) {
        String sortModel = getSortModel(attributeValueEntities);
        return getModelHash(sortModel);
    }

    /**
     * 获取model的hash值
     *
     * @param model 规格
     * @return hash值
     */
    public static String getModelHash(String model) {
        return Md5Util.md5(model);
    }
}
