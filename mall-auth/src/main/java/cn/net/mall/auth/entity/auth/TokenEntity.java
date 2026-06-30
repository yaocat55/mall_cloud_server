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

    private String username;
    private String token;
    private List<String> roles;
    private int expiresIn;
}
