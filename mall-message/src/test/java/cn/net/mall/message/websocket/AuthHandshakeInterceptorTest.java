package cn.net.mall.message.websocket;

import cn.net.mall.redis.TokenHelper;
import cn.net.mall.redis.UserTokenHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.WebSocketHandler;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
        TokenHelper tokenHelper = mock(TokenHelper.class);
        AuthHandshakeInterceptor interceptor = new AuthHandshakeInterceptor(userTokenHelper, tokenHelper);

        when(userTokenHelper.getUsernameFromToken("t1")).thenReturn("jack");
        UserDetails userDetails = new User("jack", "pwd", Collections.emptyList());
        when(tokenHelper.getUserDetailsFromUsername("jack")).thenReturn(userDetails);

        MockHttpServletRequest servletRequest = new MockHttpServletRequest("GET", "/ws");
        servletRequest.setQueryString("token=t1");
        ServerHttpRequest request = new ServletServerHttpRequest(servletRequest);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebSocketHandler handler = mock(WebSocketHandler.class);

        interceptor.beforeHandshake(request, response, handler, new HashMap<>());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(userDetails, authentication.getPrincipal());
    }

    @Test
    public void should_not_set_authentication_when_token_missing() {
        UserTokenHelper userTokenHelper = mock(UserTokenHelper.class);
        TokenHelper tokenHelper = mock(TokenHelper.class);
        AuthHandshakeInterceptor interceptor = new AuthHandshakeInterceptor(userTokenHelper, tokenHelper);

        MockHttpServletRequest servletRequest = new MockHttpServletRequest("GET", "/ws");
        ServerHttpRequest request = new ServletServerHttpRequest(servletRequest);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebSocketHandler handler = mock(WebSocketHandler.class);

        interceptor.beforeHandshake(request, response, handler, new HashMap<>());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
    }
}
