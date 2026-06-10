package cn.net.mall.auth.entity.auth;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户查询条件实体
 *
 * @date 2024-01-08 17:18:18
 */
@Schema(name = "用户查询条件实体")
@Data
public class UserConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(name = "ID")
    private Long id;

    /**
     * ID集合
     */
    private List<Long> idList;

    /**
     * 头像
     */
    @Schema(name = "头像")
    private Long avatarId;

    /**
     * 邮箱
     */
    @Schema(name = "邮箱")
    private String email;

    /**
     * 密码
     */
    @Schema(name = "密码")
    private String password;

    /**
     * 用户名
     */
    @Schema(name = "用户名")
    private String userName;

    /**
     * 部门ID
     */
    @Schema(name = "部门ID")
    private Long deptId;

    /**
     * 手机号码
     */
    @Schema(name = "手机号码")
    private String phone;

    /**
     * 岗位ID
     */
    @Schema(name = "岗位ID")
    private Long jobId;

    /**
     * 最后修改密码的日期
     */
    @Schema(name = "最后修改密码的日期")
    private Date lastChangePasswordTime;

    /**
     * 别名
     */
    @Schema(name = "别名")
    private String nickName;

    /**
     * 性别 1：男 2：女
     */
    @Schema(name = "性别 1：男 2：女")
    private Integer sex;

    /**
     * 有效状态 1:有效 0:无效
     */
    @Schema(name = "有效状态 1:有效 0:无效")
    private Boolean validStatus;

    /**
     * 创建人ID
     */
    @Schema(name = "创建人ID")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @Schema(name = "创建人名称")
    private String createUserName;

    /**
     * 修改人ID
     */
    @Schema(name = "修改人ID")
    private Long updateUserId;

    /**
     * 修改人名称
     */
    @Schema(name = "修改人名称")
    private String updateUserName;

    /**
     * 修改时间
     */
    @Schema(name = "修改时间")
    private Date updateTime;

    /**
     * 是否删除 1：已删除 0：未删除
     */
    @Schema(name = "是否删除 1：已删除 0：未删除")
    private Integer isDel;

    /**
     * 最后登录城市
     */
    @Schema(name = "最后登录城市")
    private String lastLoginCity;
}
