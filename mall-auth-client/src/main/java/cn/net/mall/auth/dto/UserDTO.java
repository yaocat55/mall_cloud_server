package cn.net.mall.auth.dto;

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
@Schema(description = "用户基本信息 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO extends BaseEntity {


    /**
     * 头像
     */
    @Schema(description = "头像", example = "0")
    private Long avatarId;


    /**
     * 头像地址
     */
    @Schema(description = "头像URL", example = "-")
    private String avatarUrl;

    /**
     * 邮箱
     */
    @NotEmpty(message = "邮箱不能为空")
    @Schema(description = "邮箱", example = "admin@mall.com")
    private String email;
    /**
     * 用户名
     */
    @NotEmpty(message = "用户名不能为空")
    @Schema(description = "用户名", example = "admin")
    private String userName;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称", example = "-")
    private String deptName;

    /**
     * 手机号码
     */
    @Schema(description = "手机号", example = "13800138000")
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
    @Schema(description = "昵称", example = "-")
    private String nickName;

    /**
     * 性别 1：男 2：女
     */
    @Schema(description = "性别：0-未知 1-男 2-女", example = "1")
    private Integer sex;

    /**
     * 有效状态 1:有效 0:无效
     */
    @Schema(description = "状态：1-启用 0-禁用", example = "1")
    private Boolean validStatus;

    /**
     * 最后登录城市
     */
    @Schema(description = "最后登录城市", example = "-")
    private String lastLoginCity;

    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间", example = "2024-01-01")
    private Date lastLoginTime;

    /**
     * 用户登录token
     */
    @Schema(description = "用户登录token", example = "eyJhbGciOiJIUzI1NiJ9.xxx")
    private String token;

    /**
     * 生日
     */
    @Schema(description = "生日", example = "-")
    private String birthday;
}


