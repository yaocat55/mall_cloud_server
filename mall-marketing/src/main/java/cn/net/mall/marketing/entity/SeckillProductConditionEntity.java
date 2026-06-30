package cn.net.mall.marketing.entity;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 秒杀商品查询条件实体
 *
 * @date 2024-07-08 10:57:31
 */
@Schema(description = "秒杀商品查询条件实体")
@Data
public class SeckillProductConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

    /**
     * ID集合
     */
    @Schema(description = "系统ID列表")
    private List<Long> idList;

    /**
     * 商品ID
     */
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    /**
     * 预扣库存
     */
    @Schema(description = "预扣库存", example = "0")
    private Integer withHoldQuantity;

    /**
     * 实际剩余库存
     */
    @Schema(description = "实际剩余库存", example = "100")
    private Integer remainQuantity;

    /**
     * 秒杀价格
     */
    @Schema(description = "秒杀价格", example = "99.99")
    private BigDecimal price;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID", example = "1")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @Schema(description = "创建人名称", example = "admin")
    private String createUserName;

    /**
     * 创建日期
     */
    @Schema(description = "创建日期", example = "2024-01-01 00:00:00")
    private Date createTime;

    /**
     * 修改人ID
     */
    @Schema(description = "修改人ID", example = "1")
    private Long updateUserId;

    /**
     * 修改人名称
     */
    @Schema(description = "修改人名称", example = "admin")
    private String updateUserName;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间", example = "2024-01-01 00:00:00")
    private Date updateTime;

    /**
     * 是否删除 1：已删除 0：未删除
     */
    @Schema(description = "是否删除 1：已删除 0：未删除", example = "0")
    private Integer isDel;

    /**
     * 秒杀开始时间
     */
    @Schema(description = "秒杀开始时间", example = "2024-01-01")
    private Date startTime;

    /**
     * 秒杀结束时间
     */
    @Schema(description = "秒杀结束时间", example = "2024-01-01")
    private Date endTime;
}
