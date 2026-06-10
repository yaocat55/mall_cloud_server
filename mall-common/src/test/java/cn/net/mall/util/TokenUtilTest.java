package cn.net.mall.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TokenUtilTest {

    private static final String TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXNhbiIsImV4cCI6MTcwNTAzOTA3N30.DZV6CZYGla74CZaXU1sqnX9R_x5YxfTM-DWObURn3Uhr1E88XsOxOz8F_MDfh8AaVFm87zlGXAENC8soZNz0Qw";
    private static final String BEARER_TOKEN = "Bearer " + TOKEN;
    private static final String BASIC_TOKEN = "Basic@token"; // Old format

    @Test
    void testGetTokenForAuthorization_Bearer() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(TokenUtil.AUTHORIZATION)).thenReturn(BEARER_TOKEN);

        String result = TokenUtil.getTokenForAuthorization(request);
        assertEquals(TOKEN, result);
    }

    @Test
    void testGetTokenForAuthorization_Null() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(TokenUtil.AUTHORIZATION)).thenReturn(null);

        String result = TokenUtil.getTokenForAuthorization(request);
        assertNull(result);
    }

    @Test
    void testGetTokenForAuthorization_InvalidPrefix() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(TokenUtil.AUTHORIZATION)).thenReturn("Invalid " + TOKEN);

        String result = TokenUtil.getTokenForAuthorization(request);
        assertNull(result);
    }

    @Test
    void testGetToken_Bearer() {
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add(TokenUtil.AUTHORIZATION, BEARER_TOKEN);
        when(request.getHeaders()).thenReturn(headers);

        String result = TokenUtil.getToken(request);
        assertEquals(TOKEN, result);
    }

    @Test
    void testGetToken_NullHeader() {
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        HttpHeaders headers = new HttpHeaders();
        when(request.getHeaders()).thenReturn(headers);

        String result = TokenUtil.getToken(request);
        assertNull(result);
    }
}
