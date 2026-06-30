package cn.net.mall.product.entity.shopping;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;
import java.util.List;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 购物车查询条件实体
 *
 * @date 2024-08-30 18:03:40
 */
@Data
@Schema(description = "购物车")

public class ShoppingCartConditionEntity extends RequestConditionEntity {

    /**
     * ID集合
    */
    @Schema(description = "系统ID列表")
    private List<Long> idList;

    	/**
	 *  ID
     */
	@Schema(description = "系统ID")
	private Long id;
	    	/**
	 *  商品ID
     */
	@Schema(description = "商品ID")
	private Long productId;
	    	/**
	 *  用户ID
     */
	@Schema(description = "用户ID")
	private Long userId;
	    	/**
	 *  数量
     */
	@Schema(description = "数量")
	private Integer quantity;
	    	/**
	 *  价格
     */
	@Schema(description = "价格")
	private BigDecimal price;
	    	/**
	 *  总金额
     */
	@Schema(description = "总金额")
	private BigDecimal totalAmount;
	    	/**
	 *  创建人ID
     */
	@Schema(description = "create User Id")
	private Long createUserId;
	    	/**
	 *  创建人名称
     */
	@Schema(description = "create User Name")
	private String createUserName;
	        	/**
	 *  修改人ID
     */
	@Schema(description = "update User Id")
	private Long updateUserId;
	    	/**
	 *  修改人名称
     */
	@Schema(description = "update User Name")
	private String updateUserName;
	        	/**
	 *  是否删除 1：已删除 0：未删除
     */
	@Schema(description = "是否删除")
	private Integer isDel;
	}
