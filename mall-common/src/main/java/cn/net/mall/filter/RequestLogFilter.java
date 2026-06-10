package cn.net.mall.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;
import org.springframework.util.StopWatch;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

import static cn.net.mall.constant.TraceConstant.TRACE_ID;

/**
 * 请求日志过滤器
 *
 */
@Slf4j
public class RequestLogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 包装请求和响应，以便多次读取
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // 获取SkyWalking的TraceId，如果没有则生成一个UUID作为TraceId
        String traceId = TraceContext.traceId();
        if (traceId == null || traceId.isEmpty() || "N/A".equals(traceId) || "Ignored_Trace".equals(traceId)) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }

        // 将TraceId放入MDC，使日志输出中包含TraceId
        MDC.put(TRACE_ID, traceId);

        StopWatch stopWatch = new StopWatch();
        try {
            // 记录请求开始日志
            logRequest(requestWrapper);

            // 开始计时
            stopWatch.start();

            // 执行过滤器链
            filterChain.doFilter(requestWrapper, responseWrapper);

            // 停止计时
            stopWatch.stop();

            // 记录响应日志
            logResponse(responseWrapper, stopWatch.getTotalTimeMillis());

            // 复制响应内容到原始响应
            responseWrapper.copyBodyToResponse();
        } finally {
            // 清除MDC中的TraceId
            MDC.remove(TRACE_ID);
        }
    }

    /**
     * 记录请求日志
     */
    private void logRequest(ContentCachingRequestWrapper request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUrl = uri + (queryString != null ? "?" + queryString : "");

        log.info("请求开始 - {} {}", method, fullUrl);
        log.debug("请求头: {}", getHeadersInfo(request));

        // 对于POST、PUT等请求，记录请求体
        if ("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
            String requestBody = new String(request.getContentAsByteArray());
            if (!requestBody.isEmpty()) {
                log.debug("请求体: {}", requestBody);
            }
        }
    }

    /**
     * 记录响应日志
     */
    private void logResponse(ContentCachingResponseWrapper response, long timeMillis) {
        int status = response.getStatus();
        log.info("请求结束 - 状态码: {}, 耗时: {}ms", status, timeMillis);

        // 记录响应体，仅在DEBUG级别
        if (log.isDebugEnabled()) {
            String responseBody = new String(response.getContentAsByteArray());
            if (!responseBody.isEmpty()) {
                log.debug("响应体: {}", responseBody);
            }
        }
    }

    /**
     * 获取请求头信息
     */
    private String getHeadersInfo(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            headers.append(headerName).append(":").append(request.getHeader(headerName)).append(", ");
        });
        return headers.toString();
    }
}