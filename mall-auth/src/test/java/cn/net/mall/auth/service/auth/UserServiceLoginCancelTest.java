package cn.net.mall.auth.service.auth;

import cn.net.mall.auth.dto.UserLoginDTO;
import cn.net.mall.auth.dto.UserPhoneLoginDTO;
import cn.net.mall.auth.entity.auth.UserEntity;
import cn.net.mall.auth.mapper.auth.*;
import cn.net.mall.auth.util.PasswordUtil;
import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.workid.IdGenerateHelper;
import cn.net.mall.redis.TokenHelper;
import cn.net.mall.redis.UserTokenHelper;
import cn.net.mall.redis.RedisUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

@SpringBootTest
class UserServiceLoginCancelTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserMapper userMapper;
    @MockBean
    private UserRoleMapper userRoleMapper;
    @MockBean
    private TokenHelper tokenHelper;
    @MockBean
    private PasswordUtil passwordUtil;
    @MockBean
    private RedisUtil redisUtil;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private DeptMapper deptMapper;
    @MockBean
    private JobMapper jobMapper;
    @MockBean
    private RoleMapper roleMapper;
    @MockBean
    private IdGenerateHelper idGenerateHelper;
    @MockBean
    private UserAvatarMapper userAvatarMapper;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private cn.net.mall.basic.client.SmsRecordFeignClient smsRecordFeignClient;
    @MockBean
    private UserTokenHelper userTokenHelper;

    @Test
    void should_throw_when_login_user_is_canceled_by_isDel() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("u1");
        dto.setPassword("enc");
        dto.setCode("1234");
        dto.setUuid("uuid");

        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn("1234");
        Mockito.when(passwordUtil.decodeRsaPassword(Mockito.anyString())).thenReturn("raw");

        Authentication authentication = Mockito.mock(Authentication.class);
        JwtUserEntity principal = new JwtUserEntity();
        principal.setUsername("u1");
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);

        UserEntity user = new UserEntity();
        user.setUserName("u1");
        user.setIsDel(1);
        user.setValidStatus(true);
        Mockito.when(userMapper.findByUserName("u1")).thenReturn(user);

        BusinessException ex = Assertions.assertThrows(BusinessException.class, () -> userService.login(dto));
        Assertions.assertEquals("该账号已注销，无法登录", ex.getMessage());
    }

    @Test
    void should_throw_when_login_user_is_canceled_by_validStatus() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("u2");
        dto.setPassword("enc");
        dto.setCode("1234");
        dto.setUuid("uuid");

        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn("1234");
        Mockito.when(passwordUtil.decodeRsaPassword(Mockito.anyString())).thenReturn("raw");

        Authentication authentication = Mockito.mock(Authentication.class);
        JwtUserEntity principal = new JwtUserEntity();
        principal.setUsername("u2");
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);

        UserEntity user = new UserEntity();
        user.setUserName("u2");
        user.setIsDel(0);
        user.setValidStatus(false);
        Mockito.when(userMapper.findByUserName("u2")).thenReturn(user);

        BusinessException ex = Assertions.assertThrows(BusinessException.class, () -> userService.login(dto));
        Assertions.assertEquals("该账号已注销，无法登录", ex.getMessage());
    }

    @Test
    void should_throw_when_login_by_phone_user_is_canceled() {
        UserPhoneLoginDTO dto = new UserPhoneLoginDTO();
        dto.setPhone("13800000000");
        dto.setSmsCode("666666");

        Authentication authentication = Mockito.mock(Authentication.class);
        JwtUserEntity principal = new JwtUserEntity();
        principal.setUsername("u3");
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);

        UserEntity user = new UserEntity();
        user.setPhone("13800000000");
        user.setIsDel(1);
        user.setValidStatus(true);
        Mockito.when(userMapper.findByPhone("13800000000")).thenReturn(user);

        BusinessException ex = Assertions.assertThrows(BusinessException.class, () -> userService.loginByPhone(dto));
        Assertions.assertEquals("该账号已注销，无法登录", ex.getMessage());
    }
}

