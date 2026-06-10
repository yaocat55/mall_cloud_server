package cn.net.mall.marketing.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.marketing.entity.*;
import cn.net.mall.marketing.entity.web.CouponWebEntity;
import cn.net.mall.enums.ValidStatusEnum;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.marketing.mapper.CouponMapper;
import cn.net.mall.marketing.mapper.CouponUserProvideMapper;
import cn.net.mall.marketing.mapper.CouponUserReceiveMapper;
import cn.net.mall.product.client.ProductFeignClient;
import cn.net.mall.product.dto.ProductDTO;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.DateFormatUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.marketing.service.strategy.CouponContext;
import cn.net.mall.marketing.entity.web.OrderPriceCalculateReq;
import cn.net.mall.marketing.enums.CouponTypeEnum;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static cn.net.mall.util.DateFormatUtil.YYYY_MM_DD;


/**
 * 优惠券 服务层
 *
 * @date 2024-09-13 15:38:33
 */
@AllArgsConstructor
@Service
public class CouponService extends BaseService<CouponEntity, CouponConditionEntity> {

    private final CouponMapper couponMapper;
    private final CouponUserReceiveMapper couponUserReceiveMapper;
    private final CouponUserProvideMapper couponUserProvideMapper;
    private final ProductFeignClient productFeignClient;
    private final TransactionTemplate transactionTemplate;

    /**
     * 获取可领取的优惠券列表
     *
     * @return 优惠券列表
     */
    public List<CouponWebEntity> getObtainableCouponList() {
        CouponUserProvideConditionEntity couponUserProvideConditionEntity = new CouponUserProvideConditionEntity();
        couponUserProvideConditionEntity.setValidStatus(ValidStatusEnum.VALID.getValue());
        List<CouponUserProvideEntity> couponUserProvideEntities = couponUserProvideMapper.searchByCondition(couponUserProvideConditionEntity);
        if (CollectionUtils.isEmpty(couponUserProvideEntities)) {
            return Collections.emptyList();
        }

        List<Long> productIdList = couponUserProvideEntities.stream().filter(x -> x.getProductId() > 0)
                .map(CouponUserProvideEntity::getProductId).collect(Collectors.toList());
        List<ProductDTO> productEntities = productFeignClient.findByIds(productIdList);
        Map<Long, List<ProductDTO>> productMap = productEntities.stream().collect(Collectors.groupingBy(ProductDTO::getId));

        List<Long> couponIdList = couponUserProvideEntities.stream()
                .map(CouponUserProvideEntity::getCouponId).collect(Collectors.toList());

        CouponConditionEntity couponConditionEntity = new CouponConditionEntity();
        couponConditionEntity.setValidStatus(ValidStatusEnum.VALID.getValue());
        couponConditionEntity.setIdList(couponIdList);
        couponConditionEntity.setPageNo(0);
        List<CouponEntity> couponEntities = couponMapper.searchByCondition(couponConditionEntity);
        if (CollectionUtils.isEmpty(couponEntities)) {
            return Collections.emptyList();
        }

        List<CouponWebEntity> result = Lists.newArrayList();
        for (CouponUserProvideEntity couponUserProvideEntity : couponUserProvideEntities) {
            Optional<CouponEntity> couponOptional = couponEntities.stream().filter(x -> x.getId().equals(couponUserProvideEntity.getCouponId())).findFirst();
            if (couponOptional.isPresent()) {
                CouponEntity couponEntity = couponOptional.get();
                CouponWebEntity couponWebEntity = new CouponWebEntity();
                BeanUtil.copyProperties(couponEntity, couponWebEntity, true);
                couponWebEntity.setProductId(couponUserProvideEntity.getProductId());
                List<ProductDTO> findProductList = productMap.get(couponWebEntity.getProductId());
                if (CollectionUtils.isNotEmpty(findProductList)) {
                    couponWebEntity.setProductName(findProductList.get(0).getName());
                }
                couponWebEntity.setUserId(couponUserProvideEntity.getUserId());
                long days = DateUtil.between(couponEntity.getUseEndTime(), new Date(), DateUnit.DAY);
                couponWebEntity.setValidDays(days >= 0 ? (int) days : 0);
                couponWebEntity.setUseEndTimeStr(DateFormatUtil.parseToString(couponEntity.getUseEndTime(), YYYY_MM_DD));
                couponWebEntity.setId(couponUserProvideEntity.getId());
                couponWebEntity.setCouponId(couponUserProvideEntity.getCouponId());
                result.add(couponWebEntity);
            }
        }
        fillCurrentUserReceived(result);
        return result;
    }

    private void fillCurrentUserReceived(List<CouponWebEntity> result) {
        List<Long> couponIdList = result.stream().map(CouponWebEntity::getCouponId).distinct().collect(Collectors.toList());
        JwtUserEntity currentUserInfoOrNull = FillUserUtil.getCurrentUserInfoOrNull();
        if (Objects.isNull(currentUserInfoOrNull)) {
            return;
        }

        CouponUserReceiveConditionEntity couponUserReceiveConditionEntity = new CouponUserReceiveConditionEntity();
        couponUserReceiveConditionEntity.setUserId(currentUserInfoOrNull.getId());
        couponUserReceiveConditionEntity.setCouponIdList(couponIdList);
        List<CouponUserReceiveEntity> couponUserReceiveEntities = couponUserReceiveMapper.searchByCondition(couponUserReceiveConditionEntity);
        for (CouponWebEntity couponWebEntity : result) {
            boolean match = couponUserReceiveEntities.stream().anyMatch(x -> x.getCouponId().equals(couponWebEntity.getCouponId()));
            couponWebEntity.setCurrentUserReceived(match);
        }
    }

    /**
     * 获取某用户已经领取的优惠券列表
     *
     * @return 商品列表
     */
    public List<CouponWebEntity> getUserCouponList() {
        JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfo();
        CouponUserReceiveConditionEntity couponUserReceiveConditionEntity = new CouponUserReceiveConditionEntity();
        couponUserReceiveConditionEntity.setUserId(currentUserInfo.getId());
        couponUserReceiveConditionEntity.setPageNo(0);
        List<CouponUserReceiveEntity> couponUserReceiveEntities = couponUserReceiveMapper.searchByCondition(couponUserReceiveConditionEntity);
        if (CollectionUtils.isEmpty(couponUserReceiveEntities)) {
            return Collections.emptyList();
        }

        CouponConditionEntity couponConditionEntity = new CouponConditionEntity();
        couponConditionEntity.setIdList(couponUserReceiveEntities.stream().map(CouponUserReceiveEntity::getCouponId).distinct().collect(Collectors.toList()));
        List<CouponEntity> couponEntities = couponMapper.searchByCondition(couponConditionEntity);

        return couponUserReceiveEntities.stream().map(x -> {
            CouponWebEntity couponWebEntity = new CouponWebEntity();
            Optional<CouponEntity> couponEntityOptional = couponEntities.stream().filter(c -> c.getId().equals(x.getCouponId())).findFirst();
            if (couponEntityOptional.isPresent()) {
                CouponEntity couponEntity = couponEntityOptional.get();
                BeanUtil.copyProperties(couponEntity, couponWebEntity, true);
                couponWebEntity.setUseStartTimeStr(DateFormatUtil.parseToString(couponEntity.getUseStartTime()));
                couponWebEntity.setUseEndTimeStr(DateFormatUtil.parseToString(couponEntity.getUseEndTime()));
                long days = DateUtil.between(couponEntity.getUseEndTime(), new Date(), DateUnit.DAY);
                couponWebEntity.setValidDays(days >= 0 ? (int) days : 0);
            }
            return couponWebEntity;
        }).collect(Collectors.toList());
    }

    /**
     * 用户领取优惠券
     *
     * @param couponWebEntity 优惠券
     */
    public void receiveCoupon(CouponWebEntity couponWebEntity) {
        JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfo();
        CouponUserProvideEntity couponUserProvideEntity = couponUserProvideMapper.findById(couponWebEntity.getId());
        AssertUtil.notNull(couponUserProvideEntity, "该优惠券在系统中已不存在");
        AssertUtil.isTrue(ValidStatusEnum.VALID.getValue().equals(couponUserProvideEntity.getValidStatus()), "该优惠券已经变成无效状态了");

        CouponEntity couponEntity = couponMapper.findById(couponUserProvideEntity.getCouponId());
        AssertUtil.notNull(couponEntity, "该优惠券在系统中已不存在");
        AssertUtil.isTrue(ValidStatusEnum.VALID.getValue().equals(couponEntity.getValidStatus()), "该优惠券已经变成无效状态了");

        CouponUserReceiveConditionEntity couponUserReceiveConditionEntity = new CouponUserReceiveConditionEntity();
        couponUserReceiveConditionEntity.setCouponId(couponUserProvideEntity.getCouponId());
        couponUserReceiveConditionEntity.setUserId(currentUserInfo.getId());
        List<CouponUserReceiveEntity> couponUserReceiveEntities = couponUserReceiveMapper.searchByCondition(couponUserReceiveConditionEntity);
        AssertUtil.isTrue(CollectionUtils.isEmpty(couponUserReceiveEntities), "该优惠券你已经领取过");

        CouponUserReceiveEntity couponUserReceiveEntity = createCouponUserReceiveEntity(currentUserInfo, couponUserProvideEntity);

        transactionTemplate.execute((status -> {
            //加行锁
            CouponUserProvideEntity oldCouponUserProvideEntity = couponUserProvideMapper.findByIdForUpdate(couponWebEntity.getId());
            AssertUtil.notNull(oldCouponUserProvideEntity, "该优惠券在系统中已不存在");
            AssertUtil.isTrue(ValidStatusEnum.VALID.getValue().equals(oldCouponUserProvideEntity.getValidStatus()), "该优惠券已经变成无效状态了");
            oldCouponUserProvideEntity.setReceiveCount(oldCouponUserProvideEntity.getReceiveCount() + 1);
            couponUserProvideMapper.update(oldCouponUserProvideEntity);
            couponUserReceiveMapper.insert(couponUserReceiveEntity);
            return Boolean.TRUE;
        }));
    }

    private CouponUserReceiveEntity createCouponUserReceiveEntity(JwtUserEntity currentUserInfo, CouponUserProvideEntity couponUserProvideEntity) {
        CouponUserReceiveEntity couponUserReceiveEntity = new CouponUserReceiveEntity();
        couponUserReceiveEntity.setCouponId(couponUserProvideEntity.getCouponId());
        couponUserReceiveEntity.setUserId(currentUserInfo.getId());
        couponUserReceiveEntity.setUseStatus(0);
        FillUserUtil.fillCreateUserInfo(couponUserReceiveEntity);
        return couponUserReceiveEntity;
    }

    /**
     * 查询优惠券信息
     *
     * @param id 优惠券ID
     * @return 优惠券信息
     */
    public CouponEntity findById(Long id) {
        return couponMapper.findById(id);
    }

    /**
     * 根据条件分页查询优惠券列表
     *
     * @param couponConditionEntity 优惠券信息
     * @return 优惠券集合
     */
    public ResponsePageEntity<CouponEntity> searchByPage(CouponConditionEntity couponConditionEntity) {
        return super.searchByPage(couponConditionEntity);
    }

    boolean isGeneralCouponType(Integer type) {
        return Objects.equals(type, CouponTypeEnum.NORMAL_DISCOUNT.getValue());
    }

    /**
     * 新增优惠券
     *
     * @param couponEntity 优惠券信息
     * @return 结果
     */
    public int insert(CouponEntity couponEntity) {
        couponEntity.setValidStatus(ValidStatusEnum.VALID.getValue());
        return couponMapper.insert(couponEntity);
    }

    /**
     * 修改优惠券
     *
     * @param couponEntity 优惠券信息
     * @return 结果
     */
    public int update(CouponEntity couponEntity) {
        return couponMapper.update(couponEntity);
    }

    /**
     * 批量删除优惠券
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<CouponEntity> entities = couponMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "优惠券已被删除");

        CouponEntity entity = new CouponEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return couponMapper.deleteByIds(ids, entity);
    }

    /**
     * 计算订单价格
     *
     * @param req 请求参数
     * @return 价格列表
     */
    public List<BigDecimal> calculateOrderPrice(OrderPriceCalculateReq req) {
        if (req == null || CollectionUtils.isEmpty(req.getItems())) {
            return Collections.emptyList();
        }

        Set<Long> couponIds = req.getItems().stream()
                .map(OrderPriceCalculateReq.Item::getCouponId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, CouponWebEntity> couponMap = new HashMap<>();
        if (!couponIds.isEmpty()) {
            List<CouponEntity> coupons = couponMapper.findByIds(new ArrayList<>(couponIds));
            for (CouponEntity c : coupons) {
                CouponWebEntity webEntity = new CouponWebEntity();
                BeanUtil.copyProperties(c, webEntity);
                couponMap.put(c.getId(), webEntity);
            }
        }

        List<BigDecimal> result = new ArrayList<>();
        for (OrderPriceCalculateReq.Item item : req.getItems()) {
            if (item.getCouponId() != null && couponMap.containsKey(item.getCouponId())) {
                BigDecimal payPrice = CouponContext.getInstance().calcPayMoney(item.getPrice(), couponMap.get(item.getCouponId()));
                result.add(payPrice);
            } else {
                result.add(item.getPrice());
            }
        }
        return result;
    }

    /**
     * 核销优惠券
     *
     * @param couponIds 优惠券ID集合
     */
    @Transactional(rollbackFor = Exception.class)
    public void useCoupons(List<Long> couponIds) {
        if (CollectionUtils.isEmpty(couponIds)) {
            return;
        }
        JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfo();

        CouponUserReceiveConditionEntity condition = new CouponUserReceiveConditionEntity();
        condition.setUserId(currentUserInfo.getId());
        condition.setCouponIdList(couponIds.stream().distinct().collect(Collectors.toList()));
        condition.setUseStatus(0);
        List<CouponUserReceiveEntity> userCoupons = couponUserReceiveMapper.searchByCondition(condition);

        Map<Long, List<CouponUserReceiveEntity>> availableMap = userCoupons.stream()
                .collect(Collectors.groupingBy(CouponUserReceiveEntity::getCouponId));

        List<Long> distinctIds = couponIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<CouponEntity> couponEntities = couponMapper.findByIds(distinctIds);
        Map<Long, CouponEntity> couponEntityMap = new HashMap<>();
        for (CouponEntity c : couponEntities) {
            couponEntityMap.put(c.getId(), c);
        }

        for (Long couponId : distinctIds) {
            List<CouponUserReceiveEntity> available = availableMap.get(couponId);
            if (CollectionUtils.isNotEmpty(available)) {
                CouponUserReceiveEntity entity = available.remove(0);
                entity.setUseStatus(1);
                entity.setUseTime(new Date());
                couponUserReceiveMapper.update(entity);
                continue;
            }
            CouponEntity couponEntity = couponEntityMap.get(couponId);
            AssertUtil.notNull(couponEntity, "该优惠券在系统中已不存在");
            AssertUtil.isTrue(ValidStatusEnum.VALID.getValue().equals(couponEntity.getValidStatus()), "该优惠券已经变成无效状态了");
            if (isGeneralCouponType(couponEntity.getType())) {
                CouponUserReceiveEntity receiveEntity = new CouponUserReceiveEntity();
                receiveEntity.setCouponId(couponId);
                receiveEntity.setUserId(currentUserInfo.getId());
                receiveEntity.setUseStatus(1);
                receiveEntity.setUseTime(new Date());
                FillUserUtil.fillCreateUserInfo(receiveEntity);
                couponUserReceiveMapper.insert(receiveEntity);
            } else {
                AssertUtil.isTrue(false, "没有可用的优惠券: " + couponId);
            }
        }
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return couponMapper;
    }
}

