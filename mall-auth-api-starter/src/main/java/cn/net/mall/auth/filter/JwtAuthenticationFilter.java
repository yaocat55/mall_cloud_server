package cn.net.mall.auth.filter;

import cn.net.mall.redis.RedisUtil;
import cn.net.mall.util.TokenUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 认证过滤器 — 解析 Bearer Token，验签 + Redis 黑名单检查.
 *
 * <p>由 {@link cn.net.mall.auth.config.AuthSecurityAutoConfiguration} 自动注册，
 * 也可供各服务自定义 SecurityFilterChain 时直接使用。</p>
 */
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final String tokenSecret;
    private final RedisUtil redisUtil;

    public JwtAuthenticationFilter(String tokenSecret, RedisUtil redisUtil) {
        this.tokenSecret = tokenSecret;
        this.redisUtil = redisUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String token = TokenUtil.getTokenForAuthorization(req);
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = TokenUtil.parseClaimsFromToken(token, tokenSecret);
            if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Redis 黑名单检查（踢人下线 / 登出）
                String jti = claims.getId();
                String blacklisted = redisUtil.get("blacklist:" + jti);
                if (blacklisted != null) {
                    resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token 已被踢下线");
                    return;
                }
                // 构建认证信息
                String username = claims.getSubject();
                @SuppressWarnings("unchecked")
                List<String> roles = claims.get("roles", List.class);
                List<SimpleGrantedAuthority> authorities = roles != null
                        ? roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                        : List.of();

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                auth.setDetails(new WebAuthenticationDetails(req));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}
