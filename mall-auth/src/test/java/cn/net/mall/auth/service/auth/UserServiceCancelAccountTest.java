package cn.net.mall.auth.service.auth;

import cn.net.mall.auth.entity.auth.UserEntity;
import cn.net.mall.auth.mapper.auth.DeptMapper;
import cn.net.mall.auth.mapper.auth.JobMapper;
import cn.net.mall.auth.mapper.auth.RoleMapper;
import cn.net.mall.auth.mapper.auth.UserAvatarMapper;
import cn.net.mall.auth.mapper.auth.UserMapper;
import cn.net.mall.auth.mapper.auth.UserRoleMapper;
import cn.net.mall.auth.util.PasswordUtil;
import cn.net.mall.basic.client.SmsRecordFeignClient;
import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.workid.IdGenerateHelper;
import cn.net.mall.redis.TokenHelper;
import cn.net.mall.redis.UserTokenHelper;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.redis.RedisUtil;
import cn.net.mall.util.TokenUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceCancelAccountTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRoleMapper userRoleMapper;
    @Mock
    private TokenHelper tokenHelper;
    @Mock
    private PasswordUtil passwordUtil;
    @Mock
    private RedisUtil redisUtil;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private DeptMapper deptMapper;
    @Mock
    private JobMapper jobMapper;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private IdGenerateHelper idGenerateHelper;
    @Mock
    private UserAvatarMapper userAvatarMapper;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private SmsRecordFeignClient smsRecordFeignClient;
    @Mock
    private UserTokenHelper userTokenHelper;
    @Mock
    private HttpServletRequest request;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userMapper, userRoleMapper, tokenHelper, passwordUtil, redisUtil, userDetailsService,
                deptMapper, jobMapper, roleMapper, idGenerateHelper, userAvatarMapper, authenticationManager,
                smsRecordFeignClient, userTokenHelper);
    }

    @Test
    void should_cancel_account_when_request_has_token() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        when(userMapper.findById(1L)).thenReturn(userEntity);

        Claims mockClaims = mock(Claims.class);
        when(mockClaims.getId()).thenReturn("test-jti");
        when(tokenHelper.getTokenSecret()).thenReturn("test-secret");

        JwtUserEntity jwtUserEntity = new JwtUserEntity(1L, "tester", null, Collections.emptyList(), Collections.emptyList());
        try (MockedStatic<TokenUtil> tokenUtilMock = mockStatic(TokenUtil.class)) {
            tokenUtilMock.when(() -> TokenUtil.getTokenForAuthorization(request)).thenReturn("token");
            tokenUtilMock.when(() -> TokenUtil.parseClaimsFromToken("token", "test-secret")).thenReturn(mockClaims);

            FillUserUtil.mockUser(() -> {
                userService.cancelAccount(request);
                return null;
            }, jwtUserEntity);

            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userMapper).update(captor.capture());
            UserEntity updateEntity = captor.getValue();
            assertEquals(1L, updateEntity.getId());
            assertEquals(Boolean.FALSE, updateEntity.getValidStatus());
            assertEquals(1, updateEntity.getIsDel());
            assertEquals(1L, updateEntity.getUpdateUserId());
            assertEquals("tester", updateEntity.getUpdateUserName());
            verify(redisUtil).set(eq("blacklist:test-jti"), eq("1"), anyLong());
        }
    }

    @Test
    void should_throw_when_token_missing() {
        when(request.getHeader(TokenUtil.AUTHORIZATION)).thenReturn(null);

        JwtUserEntity jwtUserEntity = new JwtUserEntity(1L, "tester", null, Collections.emptyList(), Collections.emptyList());

        assertThrows(BusinessException.class, () -> FillUserUtil.mockUser(() -> {
            userService.cancelAccount(request);
            return null;
        }, jwtUserEntity));

        verify(userMapper, never()).update(any());
    }
}
