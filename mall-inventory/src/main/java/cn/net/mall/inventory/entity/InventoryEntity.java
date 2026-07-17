package cn.net.mall.inventory.entity;

import lombok.Data;
import java.util.Date;

/**
 * 库存实体.
 */
@Data
public class InventoryEntity {
    private Long id;
    private Long productId;
    private Integer quantity;
    private Integer frozen;
    private Integer available;
    private Integer saleCount;
    private Integer version;
    private Date createTime;
    private Date updateTime;
}
