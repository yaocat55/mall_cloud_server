package cn.net.mall.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * token实体
 *
 * @date 2024/1/12 下午12:54
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TokenDTO {

    /**
     * 用户名称
     */
    private String username;

    /**
     * token
     */
    private String token;

    /**
     * 角色信息
     */
    private List<String> roles;

    /**
     * 过期时间
     */
    private int expiresIn;
}
