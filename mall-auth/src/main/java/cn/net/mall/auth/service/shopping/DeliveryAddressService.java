package cn.net.mall.auth.service.shopping;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.auth.entity.shopping.web.DeliveryAddressDefaultWebEntity;
import cn.net.mall.auth.entity.shopping.web.DeliveryAddressWebEntity;
import cn.net.mall.service.BaseService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.net.mall.auth.mapper.shopping.DeliveryAddressMapper;
import cn.net.mall.auth.entity.shopping.DeliveryAddressConditionEntity;
import cn.net.mall.auth.entity.shopping.DeliveryAddressEntity;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.mapper.BaseMapper;
import lombok.AllArgsConstructor;


/**
 * 收货地址 服务层
 *
 * @date 2024-09-01 10:02:01
 */
@Service
@AllArgsConstructor
public class DeliveryAddressService extends BaseService<DeliveryAddressEntity, DeliveryAddressConditionEntity> {

    private final DeliveryAddressMapper deliveryAddressMapper;

    /**
     * 设置默认收货地址
     *
     * @param deliveryAddressWebEntity 收货地址实体
     */
    public void setDefaultDeliveryAddress(DeliveryAddressDefaultWebEntity deliveryAddressWebEntity) {
        JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfo();
        AssertUtil.notNull(currentUserInfo, "请先登录");

        DeliveryAddressEntity deliveryAddressEntity = deliveryAddressMapper.findById(deliveryAddressWebEntity.getId());
        AssertUtil.notNull(deliveryAddressEntity, "该收货地址不存在");
        deliveryAddressEntity.setIsDefault(true);
        FillUserUtil.fillUpdateUserInfo(deliveryAddressEntity);

        List<DeliveryAddressEntity> updateList = Lists.newArrayList(deliveryAddressEntity);

        DeliveryAddressConditionEntity deliveryAddressConditionEntity = new DeliveryAddressConditionEntity();
        deliveryAddressConditionEntity.setUserId(currentUserInfo.getId());
        List<DeliveryAddressEntity> deliveryAddressEntities = deliveryAddressMapper.searchByCondition(deliveryAddressConditionEntity);
        List<DeliveryAddressEntity> noDefaultList = deliveryAddressEntities.stream()
                .filter(x -> x.getIsDefault() && !x.getId().equals(deliveryAddressEntity.getId())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(noDefaultList)) {
            for (DeliveryAddressEntity addressEntity : noDefaultList) {
                addressEntity.setIsDefault(false);
                FillUserUtil.fillUpdateUserInfo(addressEntity);
            }

            updateList.addAll(noDefaultList);
        }

        deliveryAddressMapper.updateForBatch(updateList);
    }

    /**
     * 获取某用户收货地址列表
     *
     * @return 收货地址列表
     */
    public List<DeliveryAddressWebEntity> getUserDeliveryAddressList() {
        JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfo();
        DeliveryAddressConditionEntity deliveryAddressConditionEntity = new DeliveryAddressConditionEntity();
        deliveryAddressConditionEntity.setUserId(currentUserInfo.getId());
        deliveryAddressConditionEntity.setSortField(Lists.newArrayList("is_default,desc", "create_time,desc"));
        List<DeliveryAddressEntity> deliveryAddressEntities = deliveryAddressMapper.searchByCondition(deliveryAddressConditionEntity);
        if (CollectionUtils.isEmpty(deliveryAddressEntities)) {
            return Collections.emptyList();
        }
        return deliveryAddressEntities.stream().map(x -> {
            DeliveryAddressWebEntity deliveryAddressWebEntity = new DeliveryAddressWebEntity();
            BeanUtil.copyProperties(x, deliveryAddressWebEntity, false);
            return deliveryAddressWebEntity;
        }).collect(Collectors.toList());
    }

    /**
     * 获取收货地址详情
     *
     * @param id 收货地址ID
     * @return 收货地址详情
     */
    public DeliveryAddressWebEntity getDetail(Long id) {
        DeliveryAddressEntity deliveryAddressEntity = this.findById(id);
        if (Objects.isNull(deliveryAddressEntity)) {
            return null;
        }

        DeliveryAddressWebEntity deliveryAddressWebEntity = new DeliveryAddressWebEntity();
        BeanUtil.copyProperties(deliveryAddressEntity, deliveryAddressWebEntity, false);
        return deliveryAddressWebEntity;
    }

    /**
     * 查询收货地址信息
     *
     * @param id 收货地址ID
     * @return 收货地址信息
     */
    public DeliveryAddressEntity findById(Long id) {
        return deliveryAddressMapper.findById(id);
    }

    /**
     * 根据条件分页查询收货地址列表
     *
     * @param deliveryAddressConditionEntity 收货地址信息
     * @return 收货地址集合
     */
    public ResponsePageEntity<DeliveryAddressEntity> searchByPage(DeliveryAddressConditionEntity deliveryAddressConditionEntity) {
        return super.searchByPage(deliveryAddressConditionEntity);
    }

    /**
     * 新增收货地址
     *
     * @param deliveryAddressEntity 收货地址信息
     * @return 结果
     */
    public int insert(DeliveryAddressEntity deliveryAddressEntity) {
        return deliveryAddressMapper.insert(deliveryAddressEntity);
    }

    /**
     * 修改收货地址
     *
     * @param deliveryAddressEntity 收货地址信息
     * @return 结果
     */
    public int update(DeliveryAddressEntity deliveryAddressEntity) {
        return deliveryAddressMapper.update(deliveryAddressEntity);
    }

    /**
     * 保存收货地址
     *
     * @param deliveryAddressWebEntity 收货地址实体
     * @return 影响行数
     */
    public void save(DeliveryAddressWebEntity deliveryAddressWebEntity) {
        DeliveryAddressEntity deliveryAddressEntity = new DeliveryAddressEntity();
        BeanUtil.copyProperties(deliveryAddressWebEntity, deliveryAddressEntity, false);
        if (Objects.nonNull(deliveryAddressWebEntity.getId())) {
            update(deliveryAddressEntity);
        } else {
            JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfo();
            deliveryAddressEntity.setUserId(currentUserInfo.getId());
            insert(deliveryAddressEntity);
        }
    }

    /**
     * 批量删除收货地址
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<DeliveryAddressEntity> entities = deliveryAddressMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "收货地址已被删除");

        DeliveryAddressEntity entity = new DeliveryAddressEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return deliveryAddressMapper.deleteByIds(ids, entity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return deliveryAddressMapper;
    }
}
