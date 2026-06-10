package cn.net.mall.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户信息实体
 *
 * @date 2025/5/22 16:16
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserInfoDTO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 角色信息
     */
    private List<String> roles;
}