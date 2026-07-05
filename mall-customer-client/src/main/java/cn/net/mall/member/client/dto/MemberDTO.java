package cn.net.mall.customer.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "会员信息")
@Data
public class MemberDTO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "性别 1:男 2:女 0:未知")
    private Integer sex;

    @Schema(description = "生日")
    private String birthday;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "会员等级名称")
    private String levelName;

    @Schema(description = "成长值")
    private Integer growth;

    @Schema(description = "积分余额")
    private Integer points;

    @Schema(description = "累计消费金额")
    private BigDecimal totalConsumption;

    @Schema(description = "token")
    private String token;
}
