package cn.net.mall.admin.mapper.shopping;

import cn.net.mall.admin.entity.shopping.DeliveryAddressConditionEntity;
import cn.net.mall.admin.entity.shopping.DeliveryAddressEntity;

import java.util.List;

import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 收货地址 mapper
 *
 * @date 2024-09-01 10:02:01
 */
public interface DeliveryAddressMapper extends BaseMapper<DeliveryAddressEntity, DeliveryAddressConditionEntity> {
    /**
     * 查询收货地址信息
     *
     * @param id 收货地址ID
     * @return 收货地址信息
     */
    DeliveryAddressEntity findById(Long id);

    /**
     * 添加收货地址
     *
     * @param deliveryAddressEntity 收货地址信息
     * @return 结果
     */
    int insert(DeliveryAddressEntity deliveryAddressEntity);

    /**
     * 修改收货地址
     *
     * @param deliveryAddressEntity 收货地址信息
     * @return 结果
     */
    int update(DeliveryAddressEntity deliveryAddressEntity);

    /**
     * 批量删除收货地址
     *
     * @param ids    id集合
     * @param entity 收货地址实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") DeliveryAddressEntity entity);

    /**
     * 批量查询收货地址信息
     *
     * @param ids ID集合
     * @return 收货地址信息
     */
    List<DeliveryAddressEntity> findByIds(List<Long> ids);

    /**
     * 批量更新收货地址
     *
     * @param list 收货地址
     * @return 影响行数
     */
    int updateForBatch(List<DeliveryAddressEntity> list);
}
