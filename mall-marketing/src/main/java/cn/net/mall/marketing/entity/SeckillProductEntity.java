package cn.net.mall.marketing.entity;

import cn.net.mall.annotation.MaxMoney;
import cn.net.mall.annotation.MinMoney;
import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀商品实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-07-08 10:57:31
 */
@Schema(description = "秒杀商品实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SeckillProductEntity extends BaseEntity {

	/**
	 * 分类名称
	 */
	@Schema(description = "分类名称", example = "-")
	private String categoryName;

	/**
	 * 单位名称
	 */
	@Schema(description = "单位名称", example = "-")
	private String unitName;

	/**
	 * 品牌名称
	 */
	@Schema(description = "品牌名称", example = "-")
	private String brandName;

	/**
	 * 商品名称
	 */
	@Schema(description = "商品名称", example = "测试数据")
	private String name;

	/**
	 * 规格
	 */
	@Schema(description = "规格", example = "型号")
	private String model;

	/**
	 * 原价
	 */
	@Schema(description = "原价", example = "99.99")
	private BigDecimal costPrice;

	/**
	 * 商品ID
	 */
	@NotNull(message = "商品ID不能为空")
	@Schema(description = "商品ID", example = "1")
	private Long productId;

	/**
	 * 预扣库存
	 */
	@NotNull(message = "预扣库存不能为空")
	@MinMoney(value = 0, message = "预扣库存必须大于0")
	@MaxMoney(value = 10000, message = "预扣库存必须小于10000")
	@Schema(description = "预扣库存", example = "0")
	private Integer withHoldQuantity;

	/**
	 * 实际剩余库存
	 */
	@NotNull(message = "实际剩余库存不能为空")
	@MinMoney(value = 0, message = "实际剩余库存必须大于0")
	@MaxMoney(value = 10000, message = "预实际剩余库存必须小于10000")
	@Schema(description = "实际剩余库存", example = "100")
	private Integer remainQuantity;

	/**
	 * 秒杀价格
	 */
	@NotNull(message = "秒杀价格不能为空")
	@MinMoney(value = 0, message = "秒杀价格必须大于0")
	@MaxMoney(value = 10000, message = "秒杀价格必须小于10000")
	@Schema(description = "秒杀价格", example = "99.99")
	private BigDecimal price;

	/**
	 *  秒杀开始时间
	 */
	@NotNull(message = "秒杀开始时间不能为空")
	@Schema(description = "秒杀开始时间", example = "2024-01-01")
	private Date startTime;

	/**
	 *  秒杀结束时间
	 */
	@NotNull(message = "秒杀结束时间不能为空")
	@Schema(description = "秒杀结束时间", example = "2024-01-01")
	private Date endTime;
}
