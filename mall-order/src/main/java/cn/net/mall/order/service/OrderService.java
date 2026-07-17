package cn.net.mall.order.service;

import cn.net.mall.order.enums.OrderStatusEnum;
import cn.net.mall.workid.IdGenerateHelper;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.marketing.client.MarketingFeignClient;
import cn.net.mall.inventory.client.InventoryFeignClient;
import cn.net.mall.inventory.dto.InventoryConfirmDTO;
import cn.net.mall.inventory.dto.InventoryFreezeDTO;
import cn.net.mall.inventory.dto.InventoryUnfreezeDTO;
import cn.net.mall.order.dto.*;
import cn.net.mall.order.event.OrderCreatedEvent;
import cn.net.mall.product.client.ProductFeignClient;
import cn.net.mall.product.dto.*;
import cn.net.mall.marketing.dto.CouponDTO;
import cn.net.mall.marketing.dto.OrderPriceCalculateReqDTO;
import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.util.OrderCodeUtil;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.order.entity.OrderConditionEntity;
import cn.net.mall.order.entity.OrderDeliveryAddressConditionEntity;
import cn.net.mall.order.entity.OrderDeliveryAddressEntity;
import cn.net.mall.order.entity.OrderEntity;
import cn.net.mall.order.entity.OrderItemConditionEntity;
import cn.net.mall.order.entity.OrderItemEntity;
import cn.net.mall.order.es.document.OrderDocument;
import cn.net.mall.order.es.document.OrderItemDocument;
import cn.net.mall.order.es.document.OrderDeliveryAddressDocument;
import cn.net.mall.order.es.repository.OrderEsRepository;
import cn.net.mall.order.dto.TradeItemReqDTO;
import cn.net.mall.order.dto.TradeItemDTO;
import cn.net.mall.order.dto.TradeDetailDTO;
import cn.net.mall.order.mapper.OrderDeliveryAddressMapper;
import cn.net.mall.order.mapper.OrderItemMapper;
import cn.net.mall.order.mapper.OrderMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.customer.client.AddressFeignClient;
import cn.net.mall.customer.dto.AddressDTO;
import cn.net.mall.entity.RequestPageEntity;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.DateFormatUtil;
import cn.net.mall.util.BetweenTimeUtil;
import cn.net.mall.redis.RedisUtil;
import cn.net.mall.order.config.BusinessConfig;
import cn.net.mall.order.message.OrderTimeoutCancelMessage;
import cn.net.mall.order.helper.MqHelper;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单 服务层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService extends BaseService<OrderEntity, OrderConditionEntity> {

    private static final String ORDER_CODE_ID_CACHE_PREFIX = "order:code:";
    private static final long ORDER_CODE_ID_CACHE_TTL_SECONDS = 86400L;
    private static final String ORDER_TRADE_COUPON_CACHE_PREFIX = "order:trade:coupons:";
    private static final long ORDER_TRADE_COUPON_CACHE_TTL_SECONDS = 1800L;
    private static final String ORDER_COUPON_USED_PREFIX = "order:coupon:used:";

    private final OrderMapper orderMapper;

    private final OrderItemMapper orderItemMapper;

    private final OrderDeliveryAddressMapper orderDeliveryAddressMapper;

    private final IdGenerateHelper idGenerateHelper;

    private final ProductFeignClient productFeignClient;
    private final InventoryFeignClient inventoryFeignClient;

    private final MarketingFeignClient marketingFeignClient;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final OrderEsRepository orderEsRepository;

    private final AddressFeignClient addressFeignClient;

    private final RedisUtil redisUtil;
    private final MqHelper mqHelper;
    private final BusinessConfig businessConfig;

    // private final ElasticsearchOperations elasticsearchOperations;

    @Override
    protected BaseMapper<OrderEntity, OrderConditionEntity> getBaseMapper() {
        return orderMapper;
    }

    public OrderTradeCountDTO getUserOrderTradeCount() {
        return getUserOrderCount();
    }

    /**
     * 从ES查询我的订单列表
     *
     * @param page 页码
     * @param size 每页数量
     * @return 订单列表
     */
    public ResponsePageEntity<OrderDocument> searchMyOrdersFromEs(int page, int size) {
        JwtUserEntity currentUser = FillUserUtil.getCurrentUserInfo();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "orderTime"));
        Page<OrderDocument> orderPage = orderEsRepository.findByUserId(currentUser.getId(), pageable);

        RequestPageEntity requestPageEntity = new RequestPageEntity();
        requestPageEntity.setPageNo(page);
        requestPageEntity.setPageSize(size);

        return ResponsePageEntity.build(requestPageEntity, (int) orderPage.getTotalElements(), orderPage.getContent());
    }

    /**
     * 从ES按条件查询我的订单列表
     *
     * @param condition 查询条件（支持orderStatus、创建时间范围）
     * @return 订单列表
     */
    public ResponsePageEntity<OrderDocument> searchMyOrdersFromEs(OrderConditionDTO condition) {
        JwtUserEntity currentUser = FillUserUtil.getCurrentUserInfo();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }

        BetweenTimeUtil.parseTime(condition);

        int page = condition.getPageNo() == null ? 1 : condition.getPageNo();
        int size = condition.getPageSize() == null ? 10 : condition.getPageSize();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "orderTime"));

        Integer orderStatus = condition.getOrderStatus();
        Date begin = null;
        Date end = null;
        if (condition.getCreateBeginTime() != null && !condition.getCreateBeginTime().isEmpty()) {
            begin = DateFormatUtil.parseToDate(condition.getCreateBeginTime());
        }
        if (condition.getCreateEndTime() != null && !condition.getCreateEndTime().isEmpty()) {
            end = DateFormatUtil.parseToDate(condition.getCreateEndTime());
        }

        if (condition.getCode() != null && !condition.getCode().isEmpty()) {
            Page<OrderDocument> orderPage = orderEsRepository.findByUserId(currentUser.getId(), pageable);
            List<OrderDocument> filtered = orderPage.getContent().stream()
                    .filter(doc -> condition.getCode().equals(doc.getCode()))
                    .collect(Collectors.toList());
            RequestPageEntity requestPageEntity = new RequestPageEntity();
            requestPageEntity.setPageNo(page);
            requestPageEntity.setPageSize(size);
            return ResponsePageEntity.build(requestPageEntity, (int) filtered.size(), filtered);
        }

        if (condition.getKeyword() != null && !condition.getKeyword().isEmpty()) {
            String keyword = condition.getKeyword();
            Page<OrderDocument> orderPage = orderEsRepository.findByUserId(currentUser.getId(), pageable);
            List<OrderDocument> filtered = orderPage.getContent().stream().filter(doc -> {
                boolean matchCode = doc.getCode() != null && doc.getCode().contains(keyword);
                boolean matchItem = doc.getOrderItemList() != null && doc.getOrderItemList().stream()
                        .anyMatch(item -> item.getProductName() != null && item.getProductName().contains(keyword));
                return matchCode || matchItem;
            }).collect(Collectors.toList());
            RequestPageEntity requestPageEntity = new RequestPageEntity();
            requestPageEntity.setPageNo(page);
            requestPageEntity.setPageSize(size);
            return ResponsePageEntity.build(requestPageEntity, (int) filtered.size(), filtered);
        }

        Page<OrderDocument> orderPage;
        if (orderStatus != null && begin != null && end != null) {
            orderPage = orderEsRepository.findByUserIdAndOrderStatusAndOrderTimeGreaterThanEqualAndOrderTimeLessThan(currentUser.getId(), orderStatus, begin, end, pageable);
        } else if (orderStatus != null && begin != null) {
            orderPage = orderEsRepository.findByUserIdAndOrderStatusAndOrderTimeGreaterThanEqual(currentUser.getId(), orderStatus, begin, pageable);
        } else if (orderStatus != null && end != null) {
            orderPage = orderEsRepository.findByUserIdAndOrderStatusAndOrderTimeLessThan(currentUser.getId(), orderStatus, end, pageable);
        } else if (orderStatus == null && begin != null && end != null) {
            orderPage = orderEsRepository.findByUserIdAndOrderTimeGreaterThanEqualAndOrderTimeLessThan(currentUser.getId(), begin, end, pageable);
        } else if (orderStatus == null && begin != null) {
            orderPage = orderEsRepository.findByUserIdAndOrderTimeGreaterThanEqual(currentUser.getId(), begin, pageable);
        } else if (orderStatus == null && end != null) {
            orderPage = orderEsRepository.findByUserIdAndOrderTimeLessThan(currentUser.getId(), end, pageable);
        } else if (orderStatus != null) {
            orderPage = orderEsRepository.findByUserIdAndOrderStatus(currentUser.getId(), orderStatus, pageable);
        } else {
            orderPage = orderEsRepository.findByUserId(currentUser.getId(), pageable);
        }

        RequestPageEntity requestPageEntity = new RequestPageEntity();
        requestPageEntity.setPageNo(page);
        requestPageEntity.setPageSize(size);

        return ResponsePageEntity.build(requestPageEntity, (int) orderPage.getTotalElements(), orderPage.getContent());
    }

    /**
     * 查询订单列表
     *
     * @param id 订单ID
     * @return 订单信息
     */
    public OrderEntity findById(Long id) {
        OrderEntity orderEntity = orderMapper.findById(id);
        if (orderEntity != null) {
            // 查询订单明细
            OrderItemConditionEntity itemCondition = new OrderItemConditionEntity();
            itemCondition.setOrderId(id);
            orderEntity.setOrderItemList(orderItemMapper.searchByCondition(itemCondition));

            // 查询订单收货地址
            OrderDeliveryAddressConditionEntity addressCondition = new OrderDeliveryAddressConditionEntity();
            addressCondition.setOrderId(id);
            List<OrderDeliveryAddressEntity> addressList = orderDeliveryAddressMapper.searchByCondition(addressCondition);
            if (!CollectionUtils.isEmpty(addressList)) {
                orderEntity.setOrderDeliveryAddress(addressList.get(0));
            }
        }
        return orderEntity;
    }

    public TradeItemDTO getTradeItem(TradeItemReqDTO req) {
        JwtUserEntity currentUser = FillUserUtil.getCurrentUserInfo();
        OrderEntity order = findOrderByCodeWithCache(req.getTradeCode(), currentUser.getId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        OrderItemConditionEntity itemCondition = new OrderItemConditionEntity();
        itemCondition.setOrderId(order.getId());
        itemCondition.setId(req.getItemId());
        List<OrderItemEntity> items = orderItemMapper.searchByCondition(itemCondition);
        if (CollectionUtils.isEmpty(items)) {
            throw new BusinessException("订单明细不存在");
        }
        OrderItemEntity item = items.get(0);
        TradeItemDTO resp = new TradeItemDTO();
        resp.setId(item.getId());
        resp.setProductId(item.getProductId());
        resp.setProductName(item.getProductName());
        resp.setModel(item.getModel());
        resp.setPrice(item.getPrice());
        resp.setQuantity(item.getQuantity());
        resp.setAmount(item.getAmount());
        resp.setCoverUrl(item.getCoverUrl());
        return resp;
    }

    public OrderDTO getTradeByCode(String code) {
        JwtUserEntity currentUser = FillUserUtil.getCurrentUserInfo();
        OrderEntity order = findOrderByCodeWithCache(code, currentUser.getId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return BeanUtil.toBean(order, OrderDTO.class);
    }

    public TradeDetailDTO getDetailByCode(String code) {
        JwtUserEntity currentUser = FillUserUtil.getCurrentUserInfo();
        OrderEntity order = findOrderByCodeWithCache(code, currentUser.getId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        OrderItemConditionEntity itemCondition = new OrderItemConditionEntity();
        itemCondition.setOrderId(order.getId());
        List<OrderItemEntity> orderItems = orderItemMapper.searchByCondition(itemCondition);
        OrderDeliveryAddressConditionEntity addressCondition = new OrderDeliveryAddressConditionEntity();
        addressCondition.setOrderId(order.getId());
        List<OrderDeliveryAddressEntity> addressList = orderDeliveryAddressMapper.searchByCondition(addressCondition);
        TradeDetailDTO resp = new TradeDetailDTO();
        resp.setCode(order.getCode());
        resp.setUserId(order.getUserId());
        resp.setUserName(order.getUserName());
        resp.setOrderStatus(order.getOrderStatus());
        resp.setPayStatus(order.getPayStatus());
        resp.setOrderTime(order.getOrderTime());
        resp.setTotalAmount(order.getTotalAmount());
        resp.setPayAmount(order.getPaymentAmount());
        resp.setRemark(order.getRemark());
        if (!CollectionUtils.isEmpty(orderItems)) {
            resp.setOrderItemList(orderItems.stream().map(item -> {
                TradeItemDTO dto = new TradeItemDTO();
                dto.setId(item.getId());
                dto.setProductId(item.getProductId());
                dto.setProductName(item.getProductName());
                dto.setModel(item.getModel());
                dto.setPrice(item.getPrice());
                dto.setQuantity(item.getQuantity());
                dto.setAmount(item.getAmount());
                dto.setCoverUrl(item.getCoverUrl());
                return dto;
            }).collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(addressList)) {
            OrderDeliveryAddressDTO addressDTO = new OrderDeliveryAddressDTO();
            BeanUtil.copyProperties(addressList.get(0), addressDTO);
            resp.setOrderDeliveryAddress(addressDTO);
        }
        return resp;
    }

    /**
     * 创建订单
     *
     * @param orderEntity 订单信息
     * @return 订单ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(OrderEntity orderEntity) {
        // 生成订单ID
        long orderId = idGenerateHelper.nextId();
        orderEntity.setId(orderId);
        orderEntity.setOrderTime(new Date());
        orderEntity.setIsDel(0);

        // 保存订单
        orderMapper.insert(orderEntity);
        cacheOrderId(orderEntity.getCode(), orderId);

        // 保存订单明细
        List<OrderItemEntity> items = orderEntity.getOrderItemList();
        if (!CollectionUtils.isEmpty(items)) {
            for (OrderItemEntity item : items) {
                item.setOrderId(orderId);
                item.setOrderCode(orderEntity.getCode());
                item.setIsDel(0);
                // 生成明细ID，如果前端没传
                if (item.getId() == null) {
                    item.setId(idGenerateHelper.nextId());
                }
            }
            orderItemMapper.batchInsert(items);
        }

        // 保存收货地址
        OrderDeliveryAddressEntity address = orderEntity.getOrderDeliveryAddress();
        if (address != null) {
            address.setOrderId(orderId);
            address.setOrderCode(orderEntity.getCode());
            address.setUserId(orderEntity.getUserId());
            address.setUserName(orderEntity.getUserName());
            address.setIsDel(0);
            if (address.getId() == null) {
                address.setId(idGenerateHelper.nextId());
            }
            orderDeliveryAddressMapper.insert(address);
        }

        String usedJson = redisUtil.get(buildOrderCouponUsedKey(orderEntity.getCode()));
        if (usedJson != null && !usedJson.isEmpty()) {
            try {
                List<Long> couponIds = JSONUtil.toList(JSONUtil.parseArray(usedJson), Long.class);
                if (!CollectionUtils.isEmpty(couponIds)) {
                    try {
                        marketingFeignClient.useCoupons(couponIds);
                    } catch (Exception e) {
                        log.error("核销优惠券失败, couponIds={}", couponIds, e);
                    }
                }
            } catch (Exception ex) {
                log.error("解析订单优惠券缓存失败, code={}", orderEntity.getCode(), ex);
            } finally {
                try {
                    redisUtil.del(buildOrderCouponUsedKey(orderEntity.getCode()));
                } catch (Exception ignore) {
                }
            }
        }

        // 发布订单创建事件，同步ES
        applicationEventPublisher.publishEvent(new OrderCreatedEvent(this, orderEntity));

        return orderId;
    }

    /**
     * 提交订单
     *
     * @param submitDTO 提交参数
     * @return 订单编码
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderSubmitRespDTO submit(OrderSubmitDTO submitDTO) {
        // 1. 获取当前用户
        JwtUserEntity currentUser = FillUserUtil.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BusinessException("未登录");
        }

        if (submitDTO.getTradeCode() == null) {
            throw new BusinessException("订单已过期，请重新下单");
        }

        // 3. 构建订单实体
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(currentUser.getId());
        orderEntity.setUserName(currentUser.getUsername());
        orderEntity.setCode(OrderCodeUtil.generateOrderCode());
        orderEntity.setOrderStatus(1); // 1:已下单
        orderEntity.setPayStatus(1);   // 1:待支付
        orderEntity.setRemark(submitDTO.getRemark());
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<Long> shoppingCartIds = new ArrayList<>();
        List<ShoppingCartDTO> cartItems = new ArrayList<>();
        if (!CollectionUtils.isEmpty(submitDTO.getItems())) {
            shoppingCartIds = submitDTO.getItems().stream()
                    .map(x -> x.getShoppingCartId())
                    .collect(Collectors.toList());
            cartItems = productFeignClient.findShoppingCartByIds(shoppingCartIds);
            List<OrderItemEntity> orderItems = new ArrayList<>();
            if (!CollectionUtils.isEmpty(cartItems)) {
                for (ShoppingCartDTO cart : cartItems) {
                    OrderItemEntity item = new OrderItemEntity();
                    item.setProductId(cart.getProductId());
                    item.setProductName(cart.getProductName());
                    item.setModel(cart.getModel());
                    item.setPrice(cart.getPrice());
                    item.setQuantity(cart.getQuantity());
                    BigDecimal amount = BigDecimal.ZERO;
                    if (cart.getPrice() != null && cart.getQuantity() != null) {
                        amount = cart.getPrice().multiply(new BigDecimal(cart.getQuantity()));
                    }
                    item.setAmount(amount);
                    item.setCoverUrl(cart.getCoverUrl());
                    orderItems.add(item);
                    totalAmount = totalAmount.add(amount);
                }
                orderEntity.setOrderItemList(orderItems);
            }
        }
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            totalAmount = new BigDecimal("100.00");
        }
        orderEntity.setTotalAmount(totalAmount);
        BigDecimal paymentAmount = totalAmount;
        try {
            List<Long> selectedCouponIds = new ArrayList<>();
            String tradeCouponsJson = redisUtil.get(buildTradeCouponKey(submitDTO.getTradeCode()));
            if (tradeCouponsJson != null && !tradeCouponsJson.isEmpty()) {
                List<Long> parsed = JSONUtil.toList(JSONUtil.parseArray(tradeCouponsJson), Long.class);
                if (!CollectionUtils.isEmpty(parsed)) {
                    selectedCouponIds = parsed.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
                }
            }
            if (!CollectionUtils.isEmpty(cartItems) && !CollectionUtils.isEmpty(selectedCouponIds)) {
                Map<Long, Long> itemCouponMap = new HashMap<>();
                List<Long> availableItemIds = cartItems.stream()
                        .map(ShoppingCartDTO::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                for (Long couponId : selectedCouponIds) {
                    Long bestItemId = null;
                    BigDecimal bestLineDiscount = BigDecimal.ZERO;
                    for (Long itemId : availableItemIds) {
                        ShoppingCartDTO sc = cartItems.stream().filter(x -> Objects.equals(x.getId(), itemId)).findFirst().orElse(null);
                        if (sc == null || sc.getPrice() == null || sc.getQuantity() == null) {
                            continue;
                        }
                        OrderPriceCalculateReqDTO singleReq = new OrderPriceCalculateReqDTO();
                        OrderPriceCalculateReqDTO.Item singleItem = new OrderPriceCalculateReqDTO.Item();
                        singleItem.setPrice(sc.getPrice());
                        singleItem.setCouponId(couponId);
                        singleReq.setItems(java.util.Collections.singletonList(singleItem));
                        BigDecimal lineDiscount = BigDecimal.ZERO;
                        try {
                            List<BigDecimal> payUnitList = marketingFeignClient.calculateOrderPrice(singleReq);
                            BigDecimal payUnit = (!CollectionUtils.isEmpty(payUnitList) && payUnitList.get(0) != null)
                                    ? payUnitList.get(0) : sc.getPrice();
                            BigDecimal unitDiscount = sc.getPrice().subtract(payUnit);
                            if (unitDiscount.compareTo(BigDecimal.ZERO) < 0) {
                                unitDiscount = BigDecimal.ZERO;
                            }
                            lineDiscount = unitDiscount.multiply(new BigDecimal(sc.getQuantity()));
                        } catch (Exception e) {
                            log.error("试算优惠失败 couponId={} itemId={}", couponId, itemId, e);
                        }
                        if (lineDiscount.compareTo(bestLineDiscount) > 0) {
                            bestLineDiscount = lineDiscount;
                            bestItemId = itemId;
                        }
                    }
                    if (bestItemId != null && bestLineDiscount.compareTo(BigDecimal.ZERO) > 0) {
                        itemCouponMap.put(bestItemId, couponId);
                        final Long FinalBestItemId = bestItemId;
                        availableItemIds = availableItemIds.stream().filter(id -> !Objects.equals(id, FinalBestItemId)).collect(Collectors.toList());
                    }
                }
                OrderPriceCalculateReqDTO calcReq = new OrderPriceCalculateReqDTO();
                List<OrderPriceCalculateReqDTO.Item> calcItems = new ArrayList<>();
                for (ShoppingCartDTO sc : cartItems) {
                    OrderPriceCalculateReqDTO.Item ci = new OrderPriceCalculateReqDTO.Item();
                    ci.setPrice(sc.getPrice());
                    Long couponId = (sc.getId() != null) ? itemCouponMap.get(sc.getId()) : null;
                    ci.setCouponId(couponId);
                    calcItems.add(ci);
                }
                calcReq.setItems(calcItems);
                List<BigDecimal> payPrices = marketingFeignClient.calculateOrderPrice(calcReq);
                if (!CollectionUtils.isEmpty(payPrices) && payPrices.size() == cartItems.size()) {
                    BigDecimal newPayAmount = BigDecimal.ZERO;
                    for (int i = 0; i < cartItems.size(); i++) {
                        ShoppingCartDTO sc = cartItems.get(i);
                        BigDecimal unitPay = payPrices.get(i) != null ? payPrices.get(i) : (sc.getPrice() != null ? sc.getPrice() : BigDecimal.ZERO);
                        int qty = sc.getQuantity() != null ? sc.getQuantity() : 0;
                        newPayAmount = newPayAmount.add(unitPay.multiply(new BigDecimal(qty)));
                    }
                    if (newPayAmount.compareTo(BigDecimal.ZERO) >= 0) {
                        paymentAmount = newPayAmount;
                    }
                }
            }
        } catch (Exception e) {
            log.error("提交订单计算支付金额失败", e);
        }
        orderEntity.setPaymentAmount(paymentAmount);

        // 4. 保存收货地址
        if (submitDTO.getDeliveryAddressId() != null) {
            saveOrderDeliveryAddress(submitDTO, currentUser, orderEntity);
        }

        // 5. 冻结库存 + 保存订单
        //    先冻结库存，防止超卖；冻结失败时直接抛异常，订单不成立
        if (!CollectionUtils.isEmpty(cartItems)) {
            try {
                for (ShoppingCartDTO item : cartItems) {
                    InventoryFreezeDTO dto = new InventoryFreezeDTO();
                    dto.setProductId(item.getProductId());
                    dto.setQuantity(item.getQuantity());
                    dto.setOrderId(orderEntity.getId());
                    inventoryFeignClient.freeze(dto);
                }
                log.info("冻结库存成功, items={}", cartItems.size());
            } catch (Exception e) {
                log.error("冻结库存失败，下单终止", e);
                throw new BusinessException("库存不足");
            }
        }

        try {
            String tradeCouponsJson = redisUtil.get(buildTradeCouponKey(submitDTO.getTradeCode()));
            if (tradeCouponsJson != null && !tradeCouponsJson.isEmpty()) {
                try {
                    List<Long> couponIds = JSONUtil.toList(JSONUtil.parseArray(tradeCouponsJson), Long.class);
                    if (!CollectionUtils.isEmpty(couponIds)) {
                        redisUtil.set(buildOrderCouponUsedKey(orderEntity.getCode()), JSONUtil.toJsonStr(couponIds), ORDER_CODE_ID_CACHE_TTL_SECONDS);
                    }
                } catch (Exception e) {
                    log.error("读取并缓存结算优惠券失败, tradeCode={}", submitDTO.getTradeCode(), e);
                }
            }
        } catch (Exception e) {
            log.error("获取结算优惠券缓存失败, tradeCode={}", submitDTO.getTradeCode(), e);
        }

        Long orderId;
        try {
            orderId = createOrder(orderEntity);
        } catch (Exception e) {
            // 补偿：库存已扣但订单创建失败，发 MQ 通知 product 回滚库存
            log.error("订单创建失败，发起库存回滚补偿, items={}", cartItems, e);
            if (!CollectionUtils.isEmpty(cartItems)) {
                mqHelper.send(businessConfig.getStockRollbackTopic(), cartItems);
            }
            throw e;
        }
        try {
            redisUtil.del(buildTradeCouponKey(submitDTO.getTradeCode()));
        } catch (Exception ignore) {
        }

        // 6. 删除购物车中选中的商品（兜底不影响下单）
        if (!CollectionUtils.isEmpty(shoppingCartIds)) {
            try {
                productFeignClient.deleteShoppingCart(shoppingCartIds);
            } catch (Exception e) {
                log.error("删除购物车失败，ids={}", shoppingCartIds, e);
            }
        }


        sendTimeoutCancelMessage(orderId, orderEntity.getCode());

        OrderSubmitRespDTO resp = new OrderSubmitRespDTO();
        resp.setOrderId(orderId);
        resp.setCode(orderEntity.getCode());
        resp.setPayAmount(orderEntity.getPaymentAmount());
        return resp;
    }

    private void sendTimeoutCancelMessage(Long orderId, String code) {
        OrderTimeoutCancelMessage msg = new OrderTimeoutCancelMessage();
        msg.setOrderId(orderId);
        msg.setCode(code);
        mqHelper.sendDelay(
                businessConfig.getOrderTimeoutCancelTopic(),
                businessConfig.getOrderTimeoutCancelTag(),
                msg,
                businessConfig.getOrderTimeoutDelayLevel(),
                String.valueOf(orderId)
        );
    }


    private void saveOrderDeliveryAddress(OrderSubmitDTO submitDTO, JwtUserEntity currentUser, OrderEntity orderEntity) {
        AddressDTO deliveryAddressDTO = null;
        try {
            deliveryAddressDTO = addressFeignClient.getDetail(submitDTO.getDeliveryAddressId());
        } catch (Exception e) {
            log.error("获取收货地址详情失败, id={}", submitDTO.getDeliveryAddressId(), e);
        }
        if (deliveryAddressDTO == null) {
            throw new BusinessException("收货地址不存在");
        }
        if (!Objects.equals(deliveryAddressDTO.getCustomerId(), currentUser.getId())) {
            throw new BusinessException("收货地址不存在");
        }
        OrderDeliveryAddressEntity address = new OrderDeliveryAddressEntity();
        address.setReceiverName(deliveryAddressDTO.getReceiverName());
        address.setReceiverPhone(deliveryAddressDTO.getReceiverPhone());
        address.setProvince(deliveryAddressDTO.getProvince());
        address.setCity(deliveryAddressDTO.getCity());
        address.setDistrict(deliveryAddressDTO.getDistrict());
        address.setDetailAddress(deliveryAddressDTO.getDetailAddress());
        address.setPostCode(deliveryAddressDTO.getPostCode());
        orderEntity.setOrderDeliveryAddress(address);
    }

    /**
     * 获取用户订单数量统计
     *
     * @return 统计结果
     */
    public OrderTradeCountDTO getUserOrderCount() {
        JwtUserEntity currentUser = FillUserUtil.getCurrentUserInfo();
        if (currentUser == null) {
            return new OrderTradeCountDTO();
        }
        Long userId = currentUser.getId();

        OrderTradeCountDTO countDTO = new OrderTradeCountDTO();

        // 待付款 (OrderStatus=1)
        OrderConditionEntity condition = new OrderConditionEntity();
        condition.setUserId(userId);
        condition.setOrderStatus(1);
        countDTO.setToPayCount(orderMapper.searchCount(condition));

        // 待发货 (OrderStatus=2)
        condition.setOrderStatus(2);
        countDTO.setToDeliveryCount(orderMapper.searchCount(condition));

        // 待收货 (OrderStatus=3)
        condition.setOrderStatus(3);
        countDTO.setToReceiveCount(orderMapper.searchCount(condition));

        // 待评价 (OrderStatus=4)
        condition.setOrderStatus(4);
        countDTO.setToCommentCount(orderMapper.searchCount(condition));

        return countDTO;
    }

    /**
     * 取消订单
     *
     * @param operateDTO 操作参数
     */
    public void cancel(OrderOperateDTO operateDTO) {
        JwtUserEntity currentUser = FillUserUtil.getCurrentUserInfo();
        OrderEntity order = findOrderByCodeWithCache(operateDTO.getTradeCode(), currentUser.getId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getOrderStatus() != OrderStatusEnum.CREATE.getValue()) {
            throw new BusinessException("当前状态无法取消");
        }

        OrderEntity updateEntity = new OrderEntity();
        updateEntity.setId(order.getId());
        updateEntity.setOrderStatus(5); // 5:已取消
        orderMapper.update(updateEntity);
        syncEsOrderStatus(order.getId(), 5);
    }

    /**
     * 确认收货
     *
     * @param operateDTO 操作参数
     */
    public void confirmReceive(OrderOperateDTO operateDTO) {
        JwtUserEntity currentUser = FillUserUtil.getCurrentUserInfo();
        OrderEntity order = findOrderByCodeWithCache(operateDTO.getTradeCode(), currentUser.getId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        // 假设3是已发货
        if (order.getOrderStatus() != 3) {
            throw new BusinessException("当前状态无法确认收货");
        }

        OrderEntity updateEntity = new OrderEntity();
        updateEntity.setId(order.getId());
        updateEntity.setOrderStatus(4); // 4:已完成
        orderMapper.update(updateEntity);
        syncEsOrderStatus(order.getId(), 4);
    }

    public void delivery(OrderOperateDTO operateDTO) {
        JwtUserEntity currentUser = FillUserUtil.getCurrentUserInfo();
        OrderEntity order = findOrderByCodeWithCache(operateDTO.getTradeCode(), currentUser.getId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!Objects.equals(order.getOrderStatus(), OrderStatusEnum.PAY.getValue())) {
            throw new BusinessException("当前状态无法发货");
        }
        OrderEntity updateEntity = new OrderEntity();
        updateEntity.setId(order.getId());
        updateEntity.setOrderStatus(OrderStatusEnum.SHIPPED.getValue());
        orderMapper.update(updateEntity);
        syncEsOrderStatus(order.getId(), OrderStatusEnum.SHIPPED.getValue());
    }

    public void evaluate(OrderEvaluateDTO evaluateDTO) {
        JwtUserEntity currentUser = FillUserUtil.getCurrentUserInfo();
        OrderEntity order = findOrderByCodeWithCache(evaluateDTO.getTradeCode(), currentUser.getId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getOrderStatus() != 4) {
            throw new BusinessException("当前状态无法评价");
        }
        OrderTradeProductCommentDTO req = new OrderTradeProductCommentDTO();
        req.setTradeCode(evaluateDTO.getTradeCode());
        req.setOrderId(order.getId());
        List<ProductCommentSubmitDTO> commentList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(evaluateDTO.getProductCommentList())) {
            for (OrderEvaluateProductItemDTO it : evaluateDTO.getProductCommentList()) {
                ProductCommentSubmitDTO c = new ProductCommentSubmitDTO();
                Long pid = null;
                if (it.getProductId() != null && !it.getProductId().trim().isEmpty()) {
                    pid = Long.parseLong(it.getProductId().trim());
                }
                c.setProductId(pid);
                c.setRating(it.getRating());
                c.setContent(it.getContent());
                if (!CollectionUtils.isEmpty(it.getImages())) {
                    List<String> ps = it.getImages().stream()
                            .filter(x -> x != null && !x.trim().isEmpty())
                            .map(x -> x.replace("`", "").trim())
                            .collect(java.util.stream.Collectors.toList());
                    c.setPhotos(ps);
                }
                commentList.add(c);
            }
        } else if (!CollectionUtils.isEmpty(evaluateDTO.getItems())) {
            for (OrderItemEvaluateDTO it : evaluateDTO.getItems()) {
                ProductCommentSubmitDTO c = new ProductCommentSubmitDTO();
                c.setProductId(it.getProductId());
                c.setRating(it.getRating());
                c.setType(it.getType());
                c.setContent(it.getContent());
                commentList.add(c);
            }
        } else {
            throw new BusinessException("商品评价列表不能为空");
        }
        req.setProductCommentList(commentList);
        Boolean ok = Boolean.TRUE;
        try {
            ok = productFeignClient.saveProductComment(req);
        } catch (Exception e) {
            throw new BusinessException("保存商品评价失败");
        }
        if (!Boolean.TRUE.equals(ok)) {
            throw new BusinessException("保存商品评价失败");
        }
        OrderEntity updateEntity = new OrderEntity();
        updateEntity.setId(order.getId());
        updateEntity.setOrderStatus(OrderStatusEnum.COMMENT.getValue());
        orderMapper.update(updateEntity);
        syncEsOrderStatus(order.getId(), 7);
    }


    public void updateReturnStatus(OrderEntity order) {
        OrderEntity updateEntity = new OrderEntity();
        updateEntity.setId(order.getId());
        updateEntity.setOrderStatus(OrderStatusEnum.RETURN_APPLY.getValue());
        orderMapper.update(updateEntity);
        syncEsOrderStatus(order.getId(), OrderStatusEnum.RETURN_APPLY.getValue());
    }

    public List<OrderProductCommentViewDTO> getProductCommentByTradeCode(String code) {
        JwtUserEntity currentUser = FillUserUtil.getCurrentUserInfo();
        OrderEntity order = findOrderByCodeWithCache(code, currentUser.getId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        OrderItemConditionEntity itemCondition = new OrderItemConditionEntity();
        itemCondition.setOrderId(order.getId());
        List<OrderItemEntity> items = orderItemMapper.searchByCondition(itemCondition);
        if (CollectionUtils.isEmpty(items)) {
            return new ArrayList<>();
        }
        List<Long> productIds = items.stream().map(OrderItemEntity::getProductId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<ProductDTO> products = productFeignClient.findByIds(productIds);
        Map<Long, ProductDTO> productMap = products == null ? new HashMap<>() : products.stream().collect(Collectors.toMap(ProductDTO::getId, p -> p));
        ProductCommentConditionDTO commentCond = new ProductCommentConditionDTO();
        commentCond.setProductIdList(productIds);
        commentCond.setOrderId(order.getId());
        commentCond.setUserId(currentUser.getId());
        commentCond.setPageNo(0);
        ResponsePageEntity<ProductCommentDTO> commentPage = productFeignClient.searchProductComment(commentCond);
        List<ProductCommentDTO> comments = commentPage != null ? commentPage.getData() : new ArrayList<>();
        Map<Long, ProductCommentDTO> commentMap = comments.stream().collect(Collectors.toMap(ProductCommentDTO::getProductId, c -> c, (a, b) -> a));
        List<OrderProductCommentViewDTO> result = new ArrayList<>();
        for (OrderItemEntity it : items) {
            Long pid = it.getProductId();
            ProductDTO pd = productMap.get(pid);
            ProductCommentDTO cm = commentMap.get(pid);
            OrderProductCommentViewDTO view = new OrderProductCommentViewDTO();
            view.setProductId(pid);
            view.setProductName(pd != null ? pd.getName() : it.getProductName());
            view.setModel(pd != null ? pd.getModel() : it.getModel());
            view.setPrice(pd != null ? pd.getPrice() : it.getPrice());
            view.setCoverUrl(pd != null ? pd.getCoverUrl() : it.getCoverUrl());
            if (cm != null) {
                view.setCommentContent(cm.getContent());
                view.setRating(cm.getRating());
                view.setHasComment(true);
            } else {
                view.setHasComment(false);
            }
            result.add(view);
        }
        return result;
    }

    /**
     * 修改订单
     *
     * @param orderEntity 订单信息
     * @return 结果
     */
    public int update(OrderEntity orderEntity) {
        int rows = orderMapper.update(orderEntity);
        if (rows > 0 && orderEntity.getId() != null) {
            Integer newOrderStatus = orderEntity.getOrderStatus();
            Integer newPayStatus = orderEntity.getPayStatus();
            if (newOrderStatus != null || newPayStatus != null) {
                syncEsOrderStatusAndPayStatus(orderEntity.getId(), newOrderStatus, newPayStatus);
            }
            // 支付成功 → 确认扣减库存
            if (newOrderStatus != null && newOrderStatus == OrderStatusEnum.PAY.getValue()) {
                try {
                    OrderItemConditionEntity itemCond = new OrderItemConditionEntity();
                    itemCond.setOrderId(orderEntity.getId());
                    List<OrderItemEntity> items = orderItemMapper.searchByCondition(itemCond);
                    for (OrderItemEntity item : items) {
                        InventoryConfirmDTO confirmDTO = new InventoryConfirmDTO();
                        confirmDTO.setProductId(item.getProductId());
                        confirmDTO.setQuantity(item.getQuantity() != null ? item.getQuantity() : 1);
                        confirmDTO.setOrderId(orderEntity.getId());
                        inventoryFeignClient.confirm(confirmDTO);
                    }
                } catch (Exception e) {
                    log.error("确认扣减库存失败，orderId={}", orderEntity.getId(), e);
                }
            }
        }
        return rows;
    }

    /**
     * 批量删除订单
     *
     * @param ids ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        // 这里可能需要先删除明细和地址，或者逻辑删除
        // 简单起见，先只删除主表，实际业务中应该级联删除或标记删除
        OrderEntity entity = new OrderEntity();
        // 设置更新人等信息
        int rows = orderMapper.deleteByIds(ids, entity);
        if (!CollectionUtils.isEmpty(ids)) {
            for (Long id : ids) {
                try {
                    orderEsRepository.deleteById(id);
                } catch (Exception e) {
                    log.error("删除ES订单文档失败, id={}", id, e);
                }
            }
        }
        return rows;
    }

    /**
     * 自动取消：按订单ID原子取消（仅待支付且已下单）
     *
     * @param id 订单ID
     * @return 是否取消成功
     */
    public boolean cancelTimeoutById(Long id) {
        if (id == null) {
            return false;
        }
        int rows = orderMapper.cancelIfUnpaid(id, OrderStatusEnum.CANCEL.getValue());
        if (rows > 0) {
            syncEsOrderStatus(id, OrderStatusEnum.CANCEL.getValue());
            unfreezeInventory(id);
            return true;
        }
        return false;
    }

    /**
     * 自动取消：按订单编码兜底取消（查询到ID后走原子取消）
     *
     * @param code 订单编码
     * @return 是否取消成功
     */
    public boolean cancelTimeoutByCode(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        OrderConditionEntity condition = new OrderConditionEntity();
        condition.setCode(code);
        List<OrderEntity> list = orderMapper.searchByCondition(condition);
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        Long id = list.get(0).getId();
        return cancelTimeoutById(id);
    }

    /**
     * 订单确认/预览
     *
     * @param req 确认请求
     * @return 确认结果
     */
    public OrderConfirmRespDTO confirm(OrderConfirmReqDTO req) {
        OrderConfirmRespDTO resp = new OrderConfirmRespDTO();

        // 1. 获取当前用户
        JwtUserEntity currentUser = FillUserUtil.getCurrentUserInfo();

        // 2. 获取购物车商品
        List<ShoppingCartDTO> cartItems = new ArrayList<>();
        if (!CollectionUtils.isEmpty(req.getItems())) {
            List<Long> shoppingCartIds = req.getItems().stream()
                    .map(x -> x.getShoppingCartId()).collect(Collectors.toList());
            cartItems = productFeignClient.findShoppingCartByIds(shoppingCartIds);
        }
        resp.setItems(cartItems);

        // 3. 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (!CollectionUtils.isEmpty(cartItems)) {
            for (ShoppingCartDTO item : cartItems) {
                if (item.getPrice() != null && item.getQuantity() != null) {
                    totalAmount = totalAmount.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
                }
            }
        }
        resp.setTotalAmount(totalAmount);

        // 3.1 应用优惠券计算支付金额与优惠金额
        BigDecimal payAmount = totalAmount;
        BigDecimal couponAmount = BigDecimal.ZERO;
        if (!CollectionUtils.isEmpty(cartItems)) {
            Map<Long, Long> itemCouponMap = new HashMap<>();
            List<Long> orderCouponIds = req.getCouponIds();
            if (!CollectionUtils.isEmpty(orderCouponIds)) {
                orderCouponIds = orderCouponIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
                List<Long> availableItemIds = cartItems.stream()
                        .map(ShoppingCartDTO::getId)
                        .filter(Objects::nonNull)
                        .filter(id -> !itemCouponMap.containsKey(id))
                        .collect(Collectors.toList());
                for (Long couponId : orderCouponIds) {
                    Long bestItemId = null;
                    BigDecimal bestLineDiscount = BigDecimal.ZERO;
                    for (Long itemId : availableItemIds) {
                        ShoppingCartDTO sc = cartItems.stream().filter(x -> Objects.equals(x.getId(), itemId)).findFirst().orElse(null);
                        if (sc == null || sc.getPrice() == null || sc.getQuantity() == null) {
                            continue;
                        }
                        OrderPriceCalculateReqDTO singleReq = new OrderPriceCalculateReqDTO();
                        OrderPriceCalculateReqDTO.Item singleItem = new OrderPriceCalculateReqDTO.Item();
                        singleItem.setPrice(sc.getPrice());
                        singleItem.setCouponId(couponId);
                        singleReq.setItems(java.util.Collections.singletonList(singleItem));
                        BigDecimal lineDiscount = BigDecimal.ZERO;
                        try {
                            List<BigDecimal> payUnitList = marketingFeignClient.calculateOrderPrice(singleReq);
                            BigDecimal payUnit = (!CollectionUtils.isEmpty(payUnitList) && payUnitList.get(0) != null)
                                    ? payUnitList.get(0) : sc.getPrice();
                            BigDecimal unitDiscount = sc.getPrice().subtract(payUnit);
                            if (unitDiscount.compareTo(BigDecimal.ZERO) < 0) {
                                unitDiscount = BigDecimal.ZERO;
                            }
                            lineDiscount = unitDiscount.multiply(new BigDecimal(sc.getQuantity()));
                        } catch (Exception e) {
                            log.error("试算优惠失败 couponId={} itemId={}", couponId, itemId, e);
                        }
                        if (lineDiscount.compareTo(bestLineDiscount) > 0) {
                            bestLineDiscount = lineDiscount;
                            bestItemId = itemId;
                        }
                    }
                    if (bestItemId != null && bestLineDiscount.compareTo(BigDecimal.ZERO) > 0) {
                        itemCouponMap.put(bestItemId, couponId);
                        java.util.Iterator<Long> itAvail = availableItemIds.iterator();
                        while (itAvail.hasNext()) {
                            Long id = itAvail.next();
                            if (Objects.equals(id, bestItemId)) {
                                itAvail.remove();
                                break;
                            }
                        }
                    }
                }
            }
            OrderPriceCalculateReqDTO calcReq = new OrderPriceCalculateReqDTO();
            List<OrderPriceCalculateReqDTO.Item> calcItems = new ArrayList<>();
            for (ShoppingCartDTO sc : cartItems) {
                OrderPriceCalculateReqDTO.Item ci = new OrderPriceCalculateReqDTO.Item();
                ci.setPrice(sc.getPrice());
                Long couponId = (sc.getId() != null) ? itemCouponMap.get(sc.getId()) : null;
                ci.setCouponId(couponId);
                calcItems.add(ci);
            }
            calcReq.setItems(calcItems);
            try {
                List<BigDecimal> payPrices = marketingFeignClient.calculateOrderPrice(calcReq);
                if (!CollectionUtils.isEmpty(payPrices) && payPrices.size() == cartItems.size()) {
                    BigDecimal newPayAmount = BigDecimal.ZERO;
                    for (int i = 0; i < cartItems.size(); i++) {
                        ShoppingCartDTO sc = cartItems.get(i);
                        BigDecimal unitPay = payPrices.get(i) != null ? payPrices.get(i) : (sc.getPrice() != null ? sc.getPrice() : BigDecimal.ZERO);
                        int qty = sc.getQuantity() != null ? sc.getQuantity() : 0;
                        newPayAmount = newPayAmount.add(unitPay.multiply(new BigDecimal(qty)));
                    }
                    payAmount = newPayAmount;
                    couponAmount = totalAmount.subtract(payAmount);
                    if (couponAmount.compareTo(BigDecimal.ZERO) < 0) {
                        couponAmount = BigDecimal.ZERO;
                    }
                }
            } catch (Exception e) {
                log.error("计算订单价格失败", e);
            }
        }
        resp.setPayAmount(payAmount);
        resp.setCouponAmount(couponAmount);

        // 4. 获取用户地址 (Placeholder)
        List<AddressDTO> addressList = new ArrayList<>();
        try {
            addressList = addressFeignClient.getUserAddressList();
        } catch (Exception e) {
            log.error("获取用户收货地址失败", e);
        }
        resp.setAddressList(addressList);
        if (!CollectionUtils.isEmpty(addressList)) {
            AddressDTO defaultAddress = addressList.stream()
                    .filter(x -> Boolean.TRUE.equals(x.getIsDefault()))
                    .findFirst()
                    .orElse(addressList.get(0));
            resp.setDefaultAddress(defaultAddress);
        }

        // 5. 获取可用优惠券
        try {
            List<CouponDTO> coupons = marketingFeignClient.getUserCouponList();
            resp.setCoupons(coupons);
        } catch (Exception e) {
            log.error("获取优惠券失败", e);
            resp.setCoupons(new ArrayList<>());
        }

        String tradeCode = UUID.randomUUID().toString().replace("-", "");
        resp.setTradeCode(tradeCode);
        try {
            List<Long> selectCouponIds = req.getCouponIds();
            if (!CollectionUtils.isEmpty(selectCouponIds)) {
                List<Long> distinctIds = selectCouponIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(distinctIds)) {
                    redisUtil.set(buildTradeCouponKey(tradeCode), JSONUtil.toJsonStr(distinctIds), ORDER_TRADE_COUPON_CACHE_TTL_SECONDS);
                }
            }
        } catch (Exception e) {
            log.error("缓存订单确认选择的优惠券失败, tradeCode={}", tradeCode, e);
        }

        return resp;
    }

    /**
     * 释放订单占用的冻结库存（取消订单/超时释放时调用）.
     */
    private void unfreezeInventory(Long orderId) {
        try {
            OrderItemConditionEntity itemCond = new OrderItemConditionEntity();
            itemCond.setOrderId(orderId);
            List<OrderItemEntity> items = orderItemMapper.searchByCondition(itemCond);
            for (OrderItemEntity item : items) {
                InventoryUnfreezeDTO dto = new InventoryUnfreezeDTO();
                dto.setProductId(item.getProductId());
                dto.setQuantity(item.getQuantity() != null ? item.getQuantity() : 1);
                dto.setOrderId(orderId);
                inventoryFeignClient.unfreeze(dto);
            }
        } catch (Exception e) {
            log.error("释放冻结库存失败，orderId={}", orderId, e);
        }
    }

    private void syncEsOrderStatus(Long orderId, Integer newStatus) {
        try {
            java.util.Optional<OrderDocument> optional = orderEsRepository.findById(orderId);
            if (optional.isPresent()) {
                OrderDocument doc = optional.get();
                doc.setOrderStatus(newStatus);
                orderEsRepository.save(doc);
            } else {
                OrderEntity order = findById(orderId);
                if (order != null) {
                    OrderDocument document = new OrderDocument();
                    document.setId(order.getId());
                    document.setCode(order.getCode());
                    document.setUserId(order.getUserId());
                    document.setUserName(order.getUserName());
                    document.setOrderTime(order.getOrderTime());
                    document.setOrderStatus(newStatus);
                    document.setPayStatus(order.getPayStatus());
                    document.setTotalAmount(order.getTotalAmount());
                    document.setPaymentAmount(order.getPaymentAmount());
                    document.setRemark(order.getRemark());
                    document.setCreateTime(order.getCreateTime());
                    if (order.getOrderItemList() != null) {
                        List<OrderItemDocument> items = order.getOrderItemList().stream().map(item -> {
                            OrderItemDocument d = new OrderItemDocument();
                            d.setId(item.getId());
                            d.setProductId(item.getProductId());
                            d.setProductName(item.getProductName());
                            d.setModel(item.getModel());
                            d.setPrice(item.getPrice());
                            d.setQuantity(item.getQuantity());
                            d.setAmount(item.getAmount());
                            d.setCoverUrl(item.getCoverUrl());
                            return d;
                        }).collect(java.util.stream.Collectors.toList());
                        document.setOrderItemList(items);
                    }
                    if (order.getOrderDeliveryAddress() != null) {
                        OrderDeliveryAddressDocument addressDoc = new OrderDeliveryAddressDocument();
                        addressDoc.setOrderId(order.getOrderDeliveryAddress().getOrderId());
                        addressDoc.setReceiverName(order.getOrderDeliveryAddress().getReceiverName());
                        addressDoc.setReceiverPhone(order.getOrderDeliveryAddress().getReceiverPhone());
                        addressDoc.setReceiverProvince(order.getOrderDeliveryAddress().getProvince());
                        addressDoc.setReceiverCity(order.getOrderDeliveryAddress().getCity());
                        addressDoc.setReceiverRegion(order.getOrderDeliveryAddress().getDistrict());
                        addressDoc.setReceiverDetailAddress(order.getOrderDeliveryAddress().getDetailAddress());
                        addressDoc.setPostCode(order.getOrderDeliveryAddress().getPostCode());
                        document.setOrderDeliveryAddress(addressDoc);
                    }
                    orderEsRepository.save(document);
                }
            }
        } catch (Exception e) {
            log.error("同步ES订单状态失败, id={} status={}", orderId, newStatus, e);
        }
    }

    private void syncEsOrderStatusAndPayStatus(Long orderId, Integer newOrderStatus, Integer newPayStatus) {
        try {
            java.util.Optional<OrderDocument> optional = orderEsRepository.findById(orderId);
            if (optional.isPresent()) {
                OrderDocument doc = optional.get();
                if (newOrderStatus != null) {
                    doc.setOrderStatus(newOrderStatus);
                }
                if (newPayStatus != null) {
                    doc.setPayStatus(newPayStatus);
                }
                orderEsRepository.save(doc);
            } else {
                OrderEntity order = findById(orderId);
                if (order != null) {
                    OrderDocument document = new OrderDocument();
                    document.setId(order.getId());
                    document.setCode(order.getCode());
                    document.setUserId(order.getUserId());
                    document.setUserName(order.getUserName());
                    document.setOrderTime(order.getOrderTime());
                    document.setOrderStatus(newOrderStatus != null ? newOrderStatus : order.getOrderStatus());
                    document.setPayStatus(newPayStatus != null ? newPayStatus : order.getPayStatus());
                    document.setTotalAmount(order.getTotalAmount());
                    document.setPaymentAmount(order.getPaymentAmount());
                    document.setRemark(order.getRemark());
                    document.setCreateTime(order.getCreateTime());
                    if (order.getOrderItemList() != null) {
                        List<OrderItemDocument> items = order.getOrderItemList().stream().map(item -> {
                            OrderItemDocument d = new OrderItemDocument();
                            d.setId(item.getId());
                            d.setProductId(item.getProductId());
                            d.setProductName(item.getProductName());
                            d.setModel(item.getModel());
                            d.setPrice(item.getPrice());
                            d.setQuantity(item.getQuantity());
                            d.setAmount(item.getAmount());
                            d.setCoverUrl(item.getCoverUrl());
                            return d;
                        }).collect(java.util.stream.Collectors.toList());
                        document.setOrderItemList(items);
                    }
                    if (order.getOrderDeliveryAddress() != null) {
                        OrderDeliveryAddressDocument addressDoc = new OrderDeliveryAddressDocument();
                        addressDoc.setOrderId(order.getOrderDeliveryAddress().getOrderId());
                        addressDoc.setReceiverName(order.getOrderDeliveryAddress().getReceiverName());
                        addressDoc.setReceiverPhone(order.getOrderDeliveryAddress().getReceiverPhone());
                        addressDoc.setReceiverProvince(order.getOrderDeliveryAddress().getProvince());
                        addressDoc.setReceiverCity(order.getOrderDeliveryAddress().getCity());
                        addressDoc.setReceiverRegion(order.getOrderDeliveryAddress().getDistrict());
                        addressDoc.setReceiverDetailAddress(order.getOrderDeliveryAddress().getDetailAddress());
                        addressDoc.setPostCode(order.getOrderDeliveryAddress().getPostCode());
                        document.setOrderDeliveryAddress(addressDoc);
                    }
                    orderEsRepository.save(document);
                }
            }
        } catch (Exception e) {
            log.error("同步ES订单状态与支付状态失败, id={} orderStatus={} payStatus={}", orderId, newOrderStatus, newPayStatus, e);
        }
    }

    private OrderEntity findOrderByCodeWithCache(String code, Long userId) {
        Long orderId = getOrderIdFromCache(code);
        if (orderId != null) {
            OrderEntity order = orderMapper.findById(orderId);
            if (order != null && Objects.equals(order.getUserId(), userId)) {
                return order;
            }
            redisUtil.del(buildOrderCodeIdKey(code));
        }
        return findOrderByCodeAndUser(code, userId);
    }

    private OrderEntity findOrderByCodeAndUser(String code, Long userId) {
        OrderConditionEntity condition = new OrderConditionEntity();
        condition.setCode(code);
        condition.setUserId(userId);
        List<OrderEntity> list = orderMapper.searchByCondition(condition);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        OrderEntity order = list.get(0);
        cacheOrderId(code, order.getId());
        return order;
    }

    private void cacheOrderId(String code, Long orderId) {
        if (code == null || orderId == null) {
            return;
        }
        redisUtil.set(buildOrderCodeIdKey(code), String.valueOf(orderId), ORDER_CODE_ID_CACHE_TTL_SECONDS);
    }

    private Long getOrderIdFromCache(String code) {
        if (code == null) {
            return null;
        }
        String value = redisUtil.get(buildOrderCodeIdKey(code));
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            redisUtil.del(buildOrderCodeIdKey(code));
            return null;
        }
    }

    private String buildOrderCodeIdKey(String code) {
        return ORDER_CODE_ID_CACHE_PREFIX + code;
    }

    private String buildTradeCouponKey(String tradeCode) {
        return ORDER_TRADE_COUPON_CACHE_PREFIX + tradeCode;
    }

    private String buildOrderCouponUsedKey(String code) {
        return ORDER_COUPON_USED_PREFIX + code;
    }
}
