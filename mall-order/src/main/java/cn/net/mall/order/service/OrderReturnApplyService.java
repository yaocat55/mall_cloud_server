package cn.net.mall.order.service;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.workid.IdGenerateHelper;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.order.dto.OrderReturnApplyDTO;
import cn.net.mall.order.dto.OrderReturnItemDTO;
import cn.net.mall.order.dto.OrderDTO;
import cn.net.mall.order.entity.*;
import cn.net.mall.order.mapper.OrderReturnApplyMapper;
import cn.net.mall.order.mapper.OrderReturnItemMapper;
import cn.net.mall.order.mapper.OrderReturnVoucherMapper;
import cn.net.mall.order.mapper.OrderItemMapper;
import cn.net.mall.exception.BusinessException;
import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.entity.auth.JwtUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderReturnApplyService extends BaseService<OrderReturnApplyEntity, OrderReturnApplyConditionEntity> {

    private final OrderReturnApplyMapper orderReturnApplyMapper;
    private final OrderReturnItemMapper orderReturnItemMapper;
    private final OrderReturnVoucherMapper orderReturnVoucherMapper;
    private final IdGenerateHelper idGenerateHelper;
    private final OrderService orderService;
    private final OrderItemMapper orderItemMapper;

    @Override
    protected BaseMapper<OrderReturnApplyEntity, OrderReturnApplyConditionEntity> getBaseMapper() {
        return orderReturnApplyMapper;
    }


    @Transactional(rollbackFor = Exception.class)
    public Long createApply(OrderDTO order, OrderReturnApplyEntity applyEntity, List<OrderReturnItemEntity> items, List<OrderReturnVoucherEntity> vouchers) {
        JwtUserEntity current = FillUserUtil.getCurrentUserInfo();
        applyEntity.setId(idGenerateHelper.nextId());
        applyEntity.setUserId(current.getId());
        applyEntity.setCreateUserId(current.getId());
        applyEntity.setCreateUserName(current.getUsername());
        applyEntity.setCreateTime(new Date());
        applyEntity.setApplyTime(new Date());
        applyEntity.setApplyStatus(1);
        applyEntity.setIsDel(0);
        if (applyEntity.getRefundAmount() == null && items != null && !items.isEmpty()) {
            BigDecimal total = BigDecimal.ZERO;
            for (OrderReturnItemEntity it : items) {
                BigDecimal amt = it.getAmount();
                if (amt == null) {
                    amt = it.getProductPrice();
                }
                if (amt != null) {
                    total = total.add(amt);
                }
            }
            applyEntity.setRefundAmount(total);
        }
        orderReturnApplyMapper.insert(applyEntity);

        if (items != null && !items.isEmpty()) {
            for (OrderReturnItemEntity item : items) {
                item.setId(idGenerateHelper.nextId());
                item.setOrderId(applyEntity.getOrderId());
                item.setReturnApplyId(applyEntity.getId());
                item.setCreateUserId(current.getId());
                item.setCreateUserName(current.getUsername());
                item.setCreateTime(new Date());
                item.setIsDel(0);
            }
            orderReturnItemMapper.insertBatch(items);
        }
        if (vouchers != null && !vouchers.isEmpty()) {
            for (OrderReturnVoucherEntity v : vouchers) {
                v.setId(idGenerateHelper.nextId());
                v.setOrderId(applyEntity.getOrderId());
                v.setReturnApplyId(applyEntity.getId());
                v.setCreateUserId(current.getId());
                v.setCreateUserName(current.getUsername());
                v.setCreateTime(new Date());
                v.setIsDel(0);
            }
            orderReturnVoucherMapper.insertBatch(vouchers);
        }
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(order.getId());
        orderService.updateReturnStatus(orderEntity);
        return applyEntity.getId();
    }

    public ResponsePageEntity<OrderReturnApplyEntity> search(OrderReturnApplyConditionEntity condition) {
        return searchByPage(condition);
    }

    /**
     * 根据 ID 查询退货申请.
     */
    public OrderReturnApplyEntity findById(Long id) {
        return orderReturnApplyMapper.findById(id);
    }

    public OrderReturnApplyDTO getDetailByCode(String code) {
        JwtUserEntity current = FillUserUtil.getCurrentUserInfo();
        OrderReturnApplyConditionEntity cond = new OrderReturnApplyConditionEntity();
        cond.setOrderCode(code);
        if (current != null) {
            cond.setUserId(current.getId());
        }
        cond.setPageSize(0);
        List<OrderReturnApplyEntity> applies = orderReturnApplyMapper.searchByCondition(cond);
        if (applies == null || applies.isEmpty()) {
            throw new BusinessException("退货申请不存在");
        }
        OrderReturnApplyEntity apply = applies.get(0);
        OrderReturnApplyDTO dto = BeanUtil.toBean(apply, OrderReturnApplyDTO.class);
        OrderReturnItemConditionEntity itemCond = new OrderReturnItemConditionEntity();
        itemCond.setReturnApplyId(apply.getId());
        itemCond.setPageSize(0);
        List<OrderReturnItemEntity> itemEntities = orderReturnItemMapper.searchByCondition(itemCond);
        if (itemEntities != null && !itemEntities.isEmpty()) {
            List<OrderReturnItemDTO> itemDtos = itemEntities.stream()
                    .map(e -> {
                        OrderReturnItemDTO d = BeanUtil.toBean(e, OrderReturnItemDTO.class);
                        d.setProductQuantity(e.getQuantity());
                        if (e.getOrderItemId() != null) {
                            OrderItemEntity oi = orderItemMapper.findById(e.getOrderItemId());
                            if (oi != null) {
                                d.setProductPicture(oi.getCoverUrl());
                            }
                        }
                        return d;
                    })
                    .collect(Collectors.toList());
            dto.setItems(itemDtos);
        }
        OrderReturnVoucherConditionEntity voucherCond = new OrderReturnVoucherConditionEntity();
        voucherCond.setReturnApplyId(apply.getId());
        voucherCond.setPageSize(0);
        List<OrderReturnVoucherEntity> voucherEntities = orderReturnVoucherMapper.searchByCondition(voucherCond);
        if (voucherEntities != null && !voucherEntities.isEmpty()) {
            List<String> urls = voucherEntities.stream().map(OrderReturnVoucherEntity::getUrl).collect(Collectors.toList());
            dto.setVoucherUrls(urls);
        }
        if (dto.getRefundAmount() == null && dto.getItems() != null && !dto.getItems().isEmpty()) {
            BigDecimal total = BigDecimal.ZERO;
            for (OrderReturnItemDTO it : dto.getItems()) {
                BigDecimal amt = it.getAmount();
                if (amt == null) {
                    amt = it.getProductPrice();
                }
                if (amt != null) {
                    total = total.add(amt);
                }
            }
            dto.setRefundAmount(total);
        }
        return dto;
    }

    /**
     * 审批通过退货申请.
     *
     * @param entity 退货申请实体（含 id）
     * @return 影响行数
     */
    @Transactional(rollbackFor = Exception.class)
    public int approve(OrderReturnApplyEntity entity) {
        OrderReturnApplyEntity exist = orderReturnApplyMapper.findById(entity.getId());
        if (exist == null) {
            throw new BusinessException("退货申请不存在");
        }
        if (exist.getApplyStatus() != 1) {
            throw new BusinessException("当前状态不允许审批通过");
        }
        exist.setApplyStatus(2);
        exist.setAuditTime(new Date());
        FillUserUtil.fillUpdateUserInfo(exist);
        return orderReturnApplyMapper.update(exist);
    }

    /**
     * 拒绝退货申请.
     *
     * @param entity 退货申请实体（含 id 及拒绝原因）
     * @return 影响行数
     */
    @Transactional(rollbackFor = Exception.class)
    public int reject(OrderReturnApplyEntity entity) {
        OrderReturnApplyEntity exist = orderReturnApplyMapper.findById(entity.getId());
        if (exist == null) {
            throw new BusinessException("退货申请不存在");
        }
        if (exist.getApplyStatus() != 1) {
            throw new BusinessException("当前状态不允许拒绝");
        }
        exist.setApplyStatus(3);
        exist.setAuditTime(new Date());
        exist.setDescription(entity.getDescription());
        FillUserUtil.fillUpdateUserInfo(exist);
        return orderReturnApplyMapper.update(exist);
    }
}
