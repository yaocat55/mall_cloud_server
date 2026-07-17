package cn.net.mall.inventory.service;

import cn.net.mall.exception.BusinessException;
import cn.net.mall.inventory.dto.*;
import cn.net.mall.inventory.entity.InventoryBatchEntity;
import cn.net.mall.inventory.entity.InventoryEntity;
import cn.net.mall.inventory.entity.InventoryLogEntity;
import cn.net.mall.inventory.mapper.InventoryBatchMapper;
import cn.net.mall.inventory.mapper.InventoryLogMapper;
import cn.net.mall.inventory.mapper.InventoryMapper;
import cn.net.mall.redis.RedisUtil;
import cn.net.mall.workid.IdGenerateHelper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;

/**
 * 库存服务.
 *
 * <p>Redis Lua 原子扣减（主路径）→ MySQL 条件扣减（降级）</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryMapper inventoryMapper;
    private final InventoryBatchMapper inventoryBatchMapper;
    private final InventoryLogMapper inventoryLogMapper;
    private final TransactionTemplate transactionTemplate;
    private final IdGenerateHelper idGenerateHelper;
    private final RedisUtil redisUtil;
    private final StringRedisTemplate stringRedisTemplate;

    private final DefaultRedisScript<Long> freezeScript = new DefaultRedisScript<>();

    private static final String KEY_AVAILABLE = "inv:available:";
    private static final String KEY_FROZEN = "inv:frozen:";

    @PostConstruct
    public void init() {
        freezeScript.setScriptSource(new ResourceScriptSource(
                new ClassPathResource("scripts/freeze.lua")));
        freezeScript.setResultType(Long.class);

        List<InventoryEntity> all = inventoryMapper.findAll();
        for (InventoryEntity inv : all) {
            redisUtil.set(KEY_AVAILABLE + inv.getProductId(), String.valueOf(inv.getAvailable()));
            redisUtil.set(KEY_FROZEN + inv.getProductId(), String.valueOf(inv.getFrozen()));
        }
        log.info("库存预热完成，共 {} 个商品", all.size());
    }

    // ==================== 核心方法 ====================

    /**
     * 冻结库存（下单时调用）.
     *
     * 主路径：Redis Lua 脚本原子执行 DECR available + INCR frozen
     * 降级：MySQL 条件扣减（Redis 不可用时）
     */
    public void freeze(InventoryFreezeDTO dto) {
        try {
            String availKey = KEY_AVAILABLE + dto.getProductId();
            String frozenKey = KEY_FROZEN + dto.getProductId();
            int qty = dto.getQuantity();

            Long beforeAvailable = stringRedisTemplate.execute(
                    freezeScript, List.of(availKey, frozenKey), String.valueOf(qty));

            if (beforeAvailable == null || beforeAvailable < 0) {
                throw new BusinessException("库存不足");
            }

            // MySQL 最佳努力写入（乐观锁，失败不影响业务）
            InventoryEntity inv = inventoryMapper.findByProductId(dto.getProductId());
            if (inv != null) {
                inventoryMapper.decrAvailable(dto.getProductId(), qty, inv.getVersion());
                inventoryMapper.incrFrozen(dto.getProductId(), qty);
            }

            writeLog(dto.getProductId(), "FREEZE", qty, beforeAvailable.intValue(),
                    dto.getOrderId(), null);
        } catch (DataAccessException e) {
            log.warn("Redis 不可用，降级到 MySQL 扣减，productId={}", dto.getProductId());
            mysqlFreeze(dto);
        }
    }

    private void mysqlFreeze(InventoryFreezeDTO dto) {
        int rows = inventoryMapper.decrAvailableFallback(dto.getProductId(), dto.getQuantity());
        if (rows <= 0) throw new BusinessException("库存不足");
        inventoryMapper.incrFrozen(dto.getProductId(), dto.getQuantity());
        writeLog(dto.getProductId(), "FREEZE", dto.getQuantity(), 0, dto.getOrderId(), null);
    }

    /**
     * 确认扣减（支付回调时调用）.
     */
    public void confirm(InventoryConfirmDTO dto) {
        redisUtil.increment(KEY_FROZEN + dto.getProductId(), -dto.getQuantity());

        transactionTemplate.execute(status -> {
            inventoryMapper.decrFrozen(dto.getProductId(), dto.getQuantity());
            fifoOutbound(dto.getProductId(), dto.getQuantity());
            inventoryMapper.incrSaleCount(dto.getProductId(), dto.getQuantity());
            return null;
        });

        writeLog(dto.getProductId(), "CONFIRM", dto.getQuantity(), 0, dto.getOrderId(), null);
    }

    /**
     * 释放冻结（取消订单/超时时调用）.
     */
    public void unfreeze(InventoryUnfreezeDTO dto) {
        redisUtil.increment(KEY_AVAILABLE + dto.getProductId(), dto.getQuantity());
        redisUtil.increment(KEY_FROZEN + dto.getProductId(), -dto.getQuantity());

        inventoryMapper.incrAvailable(dto.getProductId(), dto.getQuantity());
        inventoryMapper.decrFrozen(dto.getProductId(), dto.getQuantity());

        writeLog(dto.getProductId(), "UNFREEZE", dto.getQuantity(), 0, dto.getOrderId(), null);
    }

    /**
     * 回库（售后退货）.
     */
    public void returnStock(InventoryReturnDTO dto) {
        redisUtil.increment(KEY_AVAILABLE + dto.getProductId(), dto.getQuantity());

        inventoryMapper.incrAvailable(dto.getProductId(), dto.getQuantity());
        inventoryMapper.incrQuantity(dto.getProductId(), dto.getQuantity());

        writeLog(dto.getProductId(), "RETURN", dto.getQuantity(), 0, dto.getOrderId(), dto.getRemark());
    }

    /**
     * 入库（采购/调拨）.
     */
    public void inbound(InventoryInboundDTO dto) {
        final InventoryBatchEntity batch = new InventoryBatchEntity();
        transactionTemplate.execute(status -> {
            batch.setId(idGenerateHelper.nextId());
            batch.setProductId(dto.getProductId());
            batch.setBatchNo(dto.getBatchNo());
            batch.setQuantity(dto.getQuantity());
            batch.setAvailable(dto.getQuantity());
            batch.setSupplier(dto.getSupplier());
            batch.setPurchasePrice(dto.getPurchasePrice());
            batch.setWarehouse(dto.getWarehouse());
            inventoryBatchMapper.insert(batch);

            inventoryMapper.incrQuantity(dto.getProductId(), dto.getQuantity());
            inventoryMapper.incrAvailable(dto.getProductId(), dto.getQuantity());
            return null;
        });

        redisUtil.increment(KEY_AVAILABLE + dto.getProductId(), dto.getQuantity());

        InventoryLogEntity log = new InventoryLogEntity();
        log.setId(idGenerateHelper.nextId());
        log.setProductId(dto.getProductId());
        log.setBatchId(batch.getId());
        log.setType("INBOUND");
        log.setQuantity(dto.getQuantity());
        log.setRemark(dto.getBatchNo());
        inventoryLogMapper.insert(log);
    }

    // ==================== 查询 ====================

    public InventoryDTO getByProductId(Long productId) {
        String key = KEY_AVAILABLE + productId;
        String redisVal = redisUtil.get(key);
        if (redisVal != null) {
            InventoryEntity inv = inventoryMapper.findByProductId(productId);
            if (inv == null) return null;
            InventoryDTO dto = new InventoryDTO();
            dto.setProductId(productId);
            dto.setAvailable(Integer.parseInt(redisVal));
            dto.setQuantity(inv.getQuantity());
            dto.setFrozen(inv.getFrozen());
            dto.setSaleCount(inv.getSaleCount());
            return dto;
        }

        InventoryEntity inv = inventoryMapper.findByProductId(productId);
        if (inv == null) return null;
        InventoryDTO dto = new InventoryDTO();
        dto.setProductId(productId);
        dto.setQuantity(inv.getQuantity());
        dto.setFrozen(inv.getFrozen());
        dto.setAvailable(inv.getAvailable());
        dto.setSaleCount(inv.getSaleCount());
        return dto;
    }

    public List<InventoryDTO> getByProductIds(List<Long> productIds) {
        return productIds.stream()
                .map(this::getByProductId)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<InventoryEntity> findAll() {
        return inventoryMapper.findAll();
    }

    private void fifoOutbound(Long productId, int quantity) {
        List<InventoryBatchEntity> batches =
                inventoryBatchMapper.findAvailableByProductId(productId);
        int remaining = quantity;
        for (InventoryBatchEntity batch : batches) {
            if (remaining <= 0) break;
            int deduct = Math.min(remaining, batch.getAvailable());
            inventoryBatchMapper.decrAvailable(batch.getId(), deduct, 0);
            remaining -= deduct;

            if (batch.getAvailable() - deduct <= 0) {
                InventoryBatchEntity update = new InventoryBatchEntity();
                update.setId(batch.getId());
                update.setStatus(2);
                inventoryBatchMapper.updateById(update);
            }
        }
        if (remaining > 0) {
            throw new BusinessException("批次库存不足，数据异常");
        }
    }

    private void writeLog(Long productId, String type, int quantity, int beforeVal,
                          Long orderId, String remark) {
        InventoryLogEntity log = new InventoryLogEntity();
        log.setId(idGenerateHelper.nextId());
        log.setProductId(productId);
        log.setType(type);
        log.setQuantity(quantity);
        log.setBeforeVal(beforeVal);
        log.setAfterVal(beforeVal - quantity);
        log.setOrderId(orderId);
        log.setRemark(remark);
        inventoryLogMapper.insert(log);
    }
}
