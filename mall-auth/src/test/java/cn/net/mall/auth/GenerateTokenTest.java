package cn.net.mall.auth;

import cn.net.mall.auth.entity.auth.UserEntity;
import cn.net.mall.auth.mapper.auth.UserMapper;
import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.helper.TokenHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

@SpringBootTest
public class GenerateTokenTest {

    @Autowired
    private TokenHelper tokenHelper;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void generateToken() {
        System.out.println("Starting token generation...");
        UserEntity user = userMapper.findByUserName("admin");
        if (user == null) {
            System.out.println("User 'admin' not found!");
            return;
        }

        JwtUserEntity jwtUser = new JwtUserEntity();
        jwtUser.setId(user.getId());
        jwtUser.setUsername(user.getUserName());
        jwtUser.setAuthorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        String token = tokenHelper.generateToken(jwtUser);
        System.out.println("==================================================");
        System.out.println("Generated Token: " + token);
        System.out.println("==================================================");
    }
}
