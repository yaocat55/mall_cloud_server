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
@Schema(description = "用户查询条件实体")
@Data
public class UserConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

    /**
     * ID集合
     */
    private List<Long> idList;

    /**
     * 头像
     */
    @Schema(description = "头像", example = "0")
    private Long avatarId;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "admin@mall.com")
    private String email;

    /**
     * 密码
     */
    @Schema(description = "密码", example = "123456")
    private String password;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin")
    private String userName;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码", example = "13800138000")
    private String phone;

    /**
     * 岗位ID
     */
    @Schema(description = "岗位ID", example = "1")
    private Long jobId;

    /**
     * 最后修改密码的日期
     */
    @Schema(description = "最后修改密码的日期", example = "2024-01-01")
    private Date lastChangePasswordTime;

    /**
     * 别名
     */
    @Schema(description = "别名", example = "-")
    private String nickName;

    /**
     * 性别 1：男 2：女
     */
    @Schema(description = "性别 1：男 2：女", example = "1")
    private Integer sex;

    /**
     * 有效状态 1:有效 0:无效
     */
    @Schema(description = "有效状态 1:有效 0:无效", example = "1")
    private Boolean validStatus;

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
     * 最后登录城市
     */
    @Schema(description = "最后登录城市", example = "-")
    private String lastLoginCity;
}
