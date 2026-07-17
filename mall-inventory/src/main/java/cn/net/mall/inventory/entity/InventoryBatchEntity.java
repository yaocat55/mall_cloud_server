package cn.net.mall.inventory.entity;

import lombok.Data;
import java.util.Date;

/**
 * 库存批次实体.
 */
@Data
public class InventoryBatchEntity {
    private Long id;
    private Long productId;
    private String batchNo;
    private Integer quantity;
    private Integer available;
    private String supplier;
    private java.math.BigDecimal purchasePrice;
    private String warehouse;
    private Date inboundTime;
    private Date expireTime;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
