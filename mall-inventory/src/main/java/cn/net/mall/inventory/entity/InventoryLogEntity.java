package cn.net.mall.inventory.entity;

import lombok.Data;
import java.util.Date;

/**
 * 库存流水实体.
 */
@Data
public class InventoryLogEntity {
    private Long id;
    private Long productId;
    private Long batchId;
    private String type;
    private Integer quantity;
    private Integer beforeVal;
    private Integer afterVal;
    private Long orderId;
    private Long shipmentId;
    private String remark;
    private Date createTime;
}
