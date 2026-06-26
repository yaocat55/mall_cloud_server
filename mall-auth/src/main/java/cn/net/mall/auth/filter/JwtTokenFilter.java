package cn.net.mall.auth.filter;

import cn.net.mall.exception.BusinessException;
import cn.net.mall.auth.util.NoLoginMap;
import cn.net.mall.redis.TokenHelper;
import cn.net.mall.util.SpringUtil;
import cn.net.mall.util.TokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;


import java.io.IOException;
import java.util.Objects;

/**
 * token过滤器
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
        System.out.println("requestURI:" + httpServletRequest.getRequestURI());
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
                String username = tokenHelper.getUsernameFromToken(token);
                if (StringUtils.hasLength(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = tokenHelper.getUserDetailsFromUsername(username);
                    if (Objects.nonNull(userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
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
