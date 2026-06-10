package cn.net.mall.product.entity;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

import java.util.List;

/**
 * 商品浏览记录查询条件实体
 *
 * @date 2024-09-04 15:12:10
 */
@Data
public class ProductViewRecordConditionEntity extends RequestConditionEntity {

    /**
     * ID集合
    */
    private List<Long> idList;

    	/**
	 *  ID
     */
	private Long id;
	    	/**
	 *  用户ID
     */
	private Long userId;
	    	/**
	 *  商品ID
     */
	private Long productId;
	    	/**
	 *  创建人ID
     */
	private Long createUserId;
	    	/**
	 *  创建人名称
     */
	private String createUserName;
	        	/**
	 *  修改人ID
     */
	private Long updateUserId;
	    	/**
	 *  修改人名称
     */
	private String updateUserName;
	        	/**
	 *  是否删除 1：已删除 0：未删除
     */
	private Integer isDel;
	}
