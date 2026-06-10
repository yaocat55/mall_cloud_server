package cn.net.mall.marketing.entity;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 优惠券查询条件实体
 *
 * @date 2024-09-13 15:38:33
 */
@Data
public class CouponConditionEntity extends RequestConditionEntity {

    /**
     * ID集合
     */
    private List<Long> idList;

    /**
     * ID
     */
    private Long id;
    /**
     * 优惠券名称
     */
    private String name;
    /**
     * 类型 1：现金券 2：阶梯满减 3：每满减 4：通用折扣 5：满N件折扣 6：满Y元折扣
     */
    private Integer type;
    /**
     * 图片地址
     */
    private String photoUrl;
    /**
     * 领券开始时间
     */
    private Date receiveStartTime;
    /**
     * 领券结束时间
     */
    private Date receiveEndTime;
    /**
     * 使用开始时间
     */
    private Date useStartTime;
    /**
     * 使用结束时间
     */
    private Date useEndTime;
    /**
     * 优惠券数量
     */
    private Integer quantity;
    /**
     * 优惠金额，比如：满100，减40， 这里就是40
     */
    private Integer offMoney;
    /**
     * 折扣，百分之多少，比如：8折，就填入80
     */
    private Integer discount;
    /**
     * 最低使用金额，比如：满100，减40， 这里就是100
     */
    private Integer minMoney;
    /**
     * 最少商品件数，比如：2件或者3件
     */
    private Integer minProductCount;
    /**
     * 每日限额，每天领取多少张优惠券
     */
    private Integer limitCountOneDay;
    /**
     * 有效状态 1:有效 0:无效
     */
    private Integer validStatus;
    /**
     * 创建人ID
     */
    private Long createUserId;
    /**
     * 创建人名称
     */
    private String createUserName;
    /**
     * 修改人ID
     */
    private Long updateUserId;
    /**
     * 修改人名称
     */
    private String updateUserName;
    /**
     * 是否删除 1：已删除 0：未删除
     */
    private Integer isDel;
}
