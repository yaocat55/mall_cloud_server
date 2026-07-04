package cn.net.mall.auth.filter;

import cn.net.mall.exception.BusinessException;
import cn.net.mall.auth.util.NoLoginMap;
import cn.net.mall.entity.auth.JwtUserEntity;
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
import org.springframework.http.HttpStatus;
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
 * 黑名单检查已由 Gateway AuthFilter 完成，auth 自身不再重复检查。
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
        if (!NoLoginMap.notExist(httpServletRequest.getRequestURI())) {
            filterChain.doFilter(httpServletRequest, servletResponse);
            return;
        }

        String token = TokenUtil.getTokenForAuthorization(httpServletRequest);

        if (Objects.isNull(token)) {
            if (NoLoginMap.notExist(httpServletRequest.getRequestURI())) {
                handleException((HttpServletRequest) servletRequest,
                        (HttpServletResponse) servletResponse,
                        new BusinessException(HttpStatus.FORBIDDEN.value(), "请先登录"));
            } else {
                filterChain.doFilter(httpServletRequest, servletResponse);
            }
            return;
        }

        TokenHelper tokenHelper = SpringUtil.getBean("tokenHelper", TokenHelper.class);

        if (Objects.nonNull(tokenHelper)) {
            try {
                Claims claims = tokenHelper.getClaimsFromToken(token);
                if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
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
                filterChain.doFilter(httpServletRequest, servletResponse);
            } catch (BusinessException e) {
                handleException((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, e);
            }
        } else {
            filterChain.doFilter(httpServletRequest, servletResponse);
        }
    }

    private void handleException(HttpServletRequest request,
                                 HttpServletResponse response,
                                 BusinessException e) throws ServletException, IOException {
        request.setAttribute(FILTER_ERROR, e);
        request.getRequestDispatcher(FILTER_ERROR_PATH).forward(request, response);
    }
}
