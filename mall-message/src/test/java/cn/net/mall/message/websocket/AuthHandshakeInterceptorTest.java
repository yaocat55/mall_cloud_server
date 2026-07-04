package cn.net.mall.message.websocket;

import cn.net.mall.redis.UserTokenHelper;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthHandshakeInterceptorTest {

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void should_set_authentication_when_token_valid() {
        UserTokenHelper userTokenHelper = mock(UserTokenHelper.class);
        AuthHandshakeInterceptor interceptor = new AuthHandshakeInterceptor(userTokenHelper);

        Claims claims = mock(Claims.class);
        when(claims.get("user_id", Long.class)).thenReturn(1L);
        when(claims.get("user_name", String.class)).thenReturn("jack");
        when(claims.get("roles", List.class)).thenReturn(List.of("ROLE_USER"));

        when(userTokenHelper.getClaimsFromToken("t1")).thenReturn(claims);

        MockHttpServletRequest servletRequest = new MockHttpServletRequest("GET", "/ws");
        servletRequest.setQueryString("token=t1");
        ServerHttpRequest request = new ServletServerHttpRequest(servletRequest);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebSocketHandler handler = mock(WebSocketHandler.class);

        interceptor.beforeHandshake(request, response, handler, new HashMap<>());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("jack", authentication.getName());
    }

    @Test
    public void should_not_set_authentication_when_token_missing() {
        UserTokenHelper userTokenHelper = mock(UserTokenHelper.class);
        AuthHandshakeInterceptor interceptor = new AuthHandshakeInterceptor(userTokenHelper);

        MockHttpServletRequest servletRequest = new MockHttpServletRequest("GET", "/ws");
        ServerHttpRequest request = new ServletServerHttpRequest(servletRequest);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebSocketHandler handler = mock(WebSocketHandler.class);

        interceptor.beforeHandshake(request, response, handler, new HashMap<>());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
    }
}
