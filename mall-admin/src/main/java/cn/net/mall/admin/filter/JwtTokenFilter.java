package cn.net.mall.admin.filter;

import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.redis.RedisUtil;
import cn.net.mall.util.TokenUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * token过滤器
 *
 * 从 JWT claims 直接解析身份（user_id / user_name / roles）。
 * 黑名单检查移回 mall-auth，仅针对 admin 操作接口（登录/登出/踢人等）。
 * 白名单路径由 SpringSecurityConfig 的 permitAll 处理，本 Filter 不拦截。
 *
 * 使用 TokenUtil.parseClaimsFromToken 解析签名，与 TokenUtil.generateToken 使用相同的
 * 签名算法（Keys.hmacShaKeyFor），确保签名验证一致。不再依赖 TokenHelper bean。
 *
 * @date 2024/1/11 下午2:10
 */
@Slf4j
public class JwtTokenFilter extends GenericFilterBean {

    private final String tokenSecret;
    private final RedisUtil redisUtil;

    public JwtTokenFilter(String tokenSecret, RedisUtil redisUtil) {
        this.tokenSecret = tokenSecret;
        this.redisUtil = redisUtil;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String token = TokenUtil.getTokenForAuthorization(httpServletRequest);

        // 无 token 时直接放行——由 Spring Security permitAll / authenticated 决定是否拒绝
        if (Objects.isNull(token)) {
            filterChain.doFilter(httpServletRequest, servletResponse);
            return;
        }

        // 使用与 TokenUtil.generateToken 相同的 Keys.hmacShaKeyFor 算法验签
        // 避免 TokenHelper.getClaimsFromToken（旧 setSigningKey API）的签名算法不一致问题
        try {
            Claims claims = TokenUtil.parseClaimsFromToken(token, tokenSecret);
            if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 黑名单检查：token 是否被踢（登出 / 踢人下线）
                String jti = claims.getId();
                if (jti != null && !jti.isEmpty()) {
                    try {
                        String blacklisted = redisUtil.get("blacklist:" + jti);
                        if (blacklisted != null) {
                            log.info("黑名单 token 被拦截, jti={}", jti);
                            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token 已被踢下线");
                            return;
                        }
                    } catch (Exception e) {
                        // Redis 不可用时降级放行，避免因 Redis 故障导致所有请求失败
                        log.warn("黑名单 Redis 不可用，降级放行", e);
                    }
                }

                // 从 JWT claims 中解析用户身份（user_id / user_name / roles）
                Long userId = claims.get("user_id", Long.class);
                String username = claims.get("user_name", String.class);

                if (userId != null && username != null && !username.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    List<String> roles = claims.get("roles", List.class);
                    List<SimpleGrantedAuthority> authorities = roles != null
                            ? roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                            : List.of();
                    JwtUserEntity jwtUserEntity = new JwtUserEntity();
                    jwtUserEntity.setId(userId);
                    jwtUserEntity.setUsername(username);
                    jwtUserEntity.setRoles(roles);
                    jwtUserEntity.setAuthorities(authorities);

                    // 将解析出的用户信息设置到 SecurityContext 中
                    // 下游接口（如 getUserDetail）通过 TokenHelper.getCurrentUsername() 读取
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(jwtUserEntity, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            // 任何 JWT 解析异常（过期、篡改、密钥不匹配等）都降级放行
            // 由 Spring Security 的 permitAll / authenticated 决定是否拒绝请求
            log.debug("JWT 解析失败，放行等待 Security 决策: {}", e.getMessage());
        }

        filterChain.doFilter(httpServletRequest, servletResponse);
    }
}
