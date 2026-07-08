package cn.net.mall.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "会员信息")
@Data
public class MemberDTO {

    @Schema(description = "ID", example = "0")
    private Long id;

    @Schema(description = "手机号", example = "string")
    private String phone;

    @Schema(description = "昵称", example = "string")
    private String nickName;

    @Schema(description = "头像URL", example = "string")
    private String avatarUrl;

    @Schema(description = "性别 1:男 2:女 0:未知", example = "0")
    private Integer sex;

    @Schema(description = "生日", example = "string")
    private String birthday;

    @Schema(description = "邮箱", example = "string")
    private String email;

    @Schema(description = "会员等级名称", example = "string")
    private String levelName;

    @Schema(description = "成长值", example = "0")
    private Integer growth;

    @Schema(description = "积分余额", example = "0")
    private Integer points;

    @Schema(description = "累计消费金额", example = "0.00")
    private BigDecimal totalConsumption;

    @Schema(description = "token", example = "string")
    private String token;
}
