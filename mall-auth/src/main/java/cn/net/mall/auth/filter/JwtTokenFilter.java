package cn.net.mall.auth.filter;

import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.redis.RedisUtil;
import cn.net.mall.redis.TokenHelper;
import cn.net.mall.util.SpringUtil;
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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * token过滤器
 *
 * 从 JWT claims 直接解析身份（user_id / user_name / roles），不再查 Redis。
 * 黑名单检查移回 mall-auth，仅针对 admin 操作接口（登录/登出/踢人等）。
 * 普通消费者（C 端）不需要踢人下线，异地登录由 mall-message 做通知提醒。
 * 白名单路径由 SpringSecurityConfig 的 permitAll 处理，本 Filter 不拦截。
 *
 * @date 2024/1/11 下午2:10
 */
@Slf4j
public class JwtTokenFilter extends GenericFilterBean {

    public final static String FILTER_ERROR = "filterError";
    public final static String FILTER_ERROR_PATH = "/throw-error";
    public final static String ERROR_PATH = "/error";

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

        TokenHelper tokenHelper = SpringUtil.getBean("tokenHelper", TokenHelper.class);

        if (Objects.nonNull(tokenHelper)) {
            try {
                Claims claims = tokenHelper.getClaimsFromToken(token);
                if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 黑名单检查：token 是否被踢（登出 / 踢人下线）
                    String jti = claims.getId();
                    if (StringUtils.hasLength(jti)) {
                        try {
                            RedisUtil redisUtil = SpringUtil.getBean("redisUtil", RedisUtil.class);
                            String blacklisted = redisUtil.get("blacklist:" + jti);
                            if (blacklisted != null) {
                                log.info("黑名单 token 被拦截, jti={}", jti);
                                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token 已被踢下线");
                                return;
                            }
                        } catch (Exception e) {
                            // Redis 不可用时降级放行
                            log.warn("黑名单 Redis 不可用，降级放行", e);
                        }
                    }

                    Long userId = claims.get("user_id", Long.class);
                    String username = claims.get("user_name", String.class);

                    if (userId == null) {
                        // 旧 token fallback：查 Redis
                        String oldUsername = claims.getSubject();
                        if (StringUtils.hasLength(oldUsername)) {
                            JwtUserEntity oldUser = (JwtUserEntity) tokenHelper.getUserDetailsFromUsername(oldUsername);
                            if (oldUser != null) {
                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(oldUser, null, oldUser.getAuthorities());
                                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                            }
                        }
                    } else {
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

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(jwtUserEntity, null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                log.debug("JWT token 解析失败，放行等待 Security 决策: {}", e.getMessage());
            }
        }

        filterChain.doFilter(httpServletRequest, servletResponse);
    }
}
