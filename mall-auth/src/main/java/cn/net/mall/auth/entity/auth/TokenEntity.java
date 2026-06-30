package cn.net.mall.auth.entity.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Token令牌")

public class TokenEntity {

    @Schema(description = "用户名")
    private String username;
    @Schema(description = "令牌")
    private String token;
    @Schema(description = "roles")
    private List<String> roles;
    @Schema(description = "expires In")
    private int expiresIn;
}
