package cn.net.mall.auth.entity.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "验证码")

public class CaptchaEntity {
    @Schema(description = "唯一标识")
    private String uuid;
    @Schema(description = "图片")
    private String img;
}
