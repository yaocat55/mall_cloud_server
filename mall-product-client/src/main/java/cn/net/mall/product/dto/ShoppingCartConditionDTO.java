package cn.net.mall.product.dto;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 购物车查询条件实体
 *
 * @date 2024-08-30 18:03:40
 */
@Data
@Schema(description = "购物车信息")

public class ShoppingCartConditionDTO extends RequestConditionEntity {

    /**
     * ID集合
    */
    @Schema(description = "id List", example = "string")
    private List<Long> idList;

    	/**
	 *  ID
     */
	@Schema(description = "系统ID", example = "0")
	private Long id;
	    	/**
	 *  商品ID
     */
	@Schema(description = "商品ID", example = "0")
	private Long productId;
	    	/**
	 *  用户ID
     */
	@Schema(description = "用户ID", example = "0")
	private Long userId;
	    	/**
	 *  数量
     */
	@Schema(description = "数量", example = "0")
	private Integer quantity;
	    	/**
	 *  价格
     */
	@Schema(description = "价格", example = "0.00")
	private BigDecimal price;
	    	/**
	 *  总金额
     */
	@Schema(description = "总金额", example = "0.00")
	private BigDecimal totalAmount;
	    	/**
	 *  创建人ID
     */
	@Schema(description = "create User Id", example = "0")
	private Long createUserId;
	    	/**
	 *  创建人名称
     */
	@Schema(description = "create User Name", example = "string")
	private String createUserName;
	        	/**
	 *  修改人ID
     */
	@Schema(description = "update User Id", example = "0")
	private Long updateUserId;
	    	/**
	 *  修改人名称
     */
	@Schema(description = "update User Name", example = "string")
	private String updateUserName;
	        	/**
	 *  是否删除 1：已删除 0：未删除
     */
	@Schema(description = "是否删除 0:未删除 1:已删除", example = "0")
	private Integer isDel;
	}
