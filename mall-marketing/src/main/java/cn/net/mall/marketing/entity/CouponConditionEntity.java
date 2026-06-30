package cn.net.mall.marketing.entity;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 优惠券查询条件实体
 *
 * @date 2024-09-13 15:38:33
 */
@Data
@Schema(description = "优惠券")

public class CouponConditionEntity extends RequestConditionEntity {

    /**
     * ID集合
     */
    @Schema(description = "系统ID列表")
    private List<Long> idList;

    /**
     * ID
     */
    @Schema(description = "系统ID")
    private Long id;
    /**
     * 优惠券名称
     */
    @Schema(description = "名称")
    private String name;
    /**
     * 类型 1：现金券 2：阶梯满减 3：每满减 4：通用折扣 5：满N件折扣 6：满Y元折扣
     */
    @Schema(description = "类型")
    private Integer type;
    /**
     * 图片地址
     */
    @Schema(description = "photo Url")
    private String photoUrl;
    /**
     * 领券开始时间
     */
    @Schema(description = "receive Start Time")
    private Date receiveStartTime;
    /**
     * 领券结束时间
     */
    @Schema(description = "receive End Time")
    private Date receiveEndTime;
    /**
     * 使用开始时间
     */
    @Schema(description = "use Start Time")
    private Date useStartTime;
    /**
     * 使用结束时间
     */
    @Schema(description = "use End Time")
    private Date useEndTime;
    /**
     * 优惠券数量
     */
    @Schema(description = "数量")
    private Integer quantity;
    /**
     * 优惠金额，比如：满100，减40， 这里就是40
     */
    @Schema(description = "off Money")
    private Integer offMoney;
    /**
     * 折扣，百分之多少，比如：8折，就填入80
     */
    @Schema(description = "discount")
    private Integer discount;
    /**
     * 最低使用金额，比如：满100，减40， 这里就是100
     */
    @Schema(description = "min Money")
    private Integer minMoney;
    /**
     * 最少商品件数，比如：2件或者3件
     */
    @Schema(description = "min Product Count")
    private Integer minProductCount;
    /**
     * 每日限额，每天领取多少张优惠券
     */
    @Schema(description = "limit Count One Day")
    private Integer limitCountOneDay;
    /**
     * 有效状态 1:有效 0:无效
     */
    @Schema(description = "valid Status")
    private Integer validStatus;
    /**
     * 创建人ID
     */
    @Schema(description = "create User Id")
    private Long createUserId;
    /**
     * 创建人名称
     */
    @Schema(description = "create User Name")
    private String createUserName;
    /**
     * 修改人ID
     */
    @Schema(description = "update User Id")
    private Long updateUserId;
    /**
     * 修改人名称
     */
    @Schema(description = "update User Name")
    private String updateUserName;
    /**
     * 是否删除 1：已删除 0：未删除
     */
    @Schema(description = "是否删除")
    private Integer isDel;
}
