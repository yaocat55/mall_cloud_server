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
@Schema(name = "秒杀商品查询条件实体")
@Data
public class SeckillProductConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(name ="ID")
    private Long id;

    /**
     * ID集合
     */
    private List<Long> idList;

    /**
     * 商品ID
     */
    @Schema(name ="商品ID")
    private Long productId;

    /**
     * 预扣库存
     */
    @Schema(name ="预扣库存")
    private Integer withHoldQuantity;

    /**
     * 实际剩余库存
     */
    @Schema(name ="实际剩余库存")
    private Integer remainQuantity;

    /**
     * 秒杀价格
     */
    @Schema(name ="秒杀价格")
    private BigDecimal price;

    /**
     * 创建人ID
     */
    @Schema(name ="创建人ID")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @Schema(name ="创建人名称")
    private String createUserName;

    /**
     * 创建日期
     */
    @Schema(name ="创建日期")
    private Date createTime;

    /**
     * 修改人ID
     */
    @Schema(name ="修改人ID")
    private Long updateUserId;

    /**
     * 修改人名称
     */
    @Schema(name ="修改人名称")
    private String updateUserName;

    /**
     * 修改时间
     */
    @Schema(name ="修改时间")
    private Date updateTime;

    /**
     * 是否删除 1：已删除 0：未删除
     */
    @Schema(name ="是否删除 1：已删除 0：未删除")
    private Integer isDel;

    /**
     * 秒杀开始时间
     */
    @Schema(name ="秒杀开始时间")
    private Date startTime;

    /**
     * 秒杀结束时间
     */
    @Schema(name ="秒杀结束时间")
    private Date endTime;
}
