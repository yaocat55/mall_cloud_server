package cn.net.mall.auth.entity.auth;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 用户实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-01-08 17:18:18
 */
@Schema(name = "用户实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEntity extends BaseEntity {


    /**
     * 头像
     */
    @Schema(name = "头像")
    private Long avatarId;

    /**
     * 邮箱
     */
    @NotEmpty(message = "邮箱不能为空")
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
    @NotEmpty(message = "用户名不能为空")
    @Schema(name = "用户名")
    private String userName;

    /**
     * 部门ID
     */
    @Schema(name = "部门ID")
    private Long deptId;

    /**
     * 部门
     */
    @Schema(name = "部门")
    private DeptEntity dept;

    /**
     * 部门名称
     */
    @Schema(name = "部门名称")
    private String deptName;

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
     * 岗位
     */
    @Schema(name = "岗位")
    private List<JobEntity> jobs;

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
     * 角色列表
     */
    @Schema(name = "角色列表")
    private List<RoleEntity> roles;

    /**
     * 最后登录城市
     */
    @Schema(name = "最后登录城市")
    private String lastLoginCity;

    /**
     * 最后登录时间
     */
    @Schema(name = "最后登录时间")
    private Date lastLoginTime;

    /**
     * 生日
     */
    @Schema(name = "生日")
    private String birthday;
}
