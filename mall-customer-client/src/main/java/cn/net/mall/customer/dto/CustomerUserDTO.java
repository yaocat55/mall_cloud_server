package cn.net.mall.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * C端用户基本信息 DTO
 */
@Schema(description = "C端用户基本信息 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerUserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "昵称", example = "小明")
    private String nickName;

    @Schema(description = "头像URL", example = "https://example.com/avatar.png")
    private String avatarUrl;

    @Schema(description = "性别 0:未知 1:男 2:女", example = "1")
    private Integer sex;

    @Schema(description = "生日", example = "1990-01-01")
    private String birthday;

    @Schema(description = "邮箱", example = "user@example.com")
    private String email;

    @Schema(description = "等级ID", example = "1")
    private Long levelId;

    @Schema(description = "等级名称", example = "黄金会员")
    private String levelName;

    @Schema(description = "成长值", example = "500")
    private Integer growth;

    @Schema(description = "积分", example = "200")
    private Integer points;
}
