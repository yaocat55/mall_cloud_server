package cn.net.mall.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CORS（跨域）响应头去重过滤器
 *
 * <p><b>辅助功能</b> —— 用于解决多个过滤器重复添加跨域头导致浏览器报错的问题。</p>
 *
 * <p>背景：Nacos 中的 {@code spring.cloud.gateway.globalcors.cors-configurations}
 * 已配置了完整的 CORS 策略。此过滤器仅作为兜底补丁，将多个 {@code Access-Control-Allow-Origin}
 * 合并为一个。如果 Nacos 配置已够用，可以删除此类。</p>
 */
@Component
public class CorsResponseHeaderFilter implements GlobalFilter, Ordered {

    private static final String ANY = "*";

    /**
     * 指定过滤器执行顺序
     * 在 NettyWriteResponseFilter 之后执行（+1），确保响应体已经写入完成
     */
    @Override
    public int getOrder() {
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER + 1;
    }

    @Override
    @SuppressWarnings("serial")
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 先执行后续过滤器链，等响应完成后处理响应头
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // 如果响应已经提交，不再处理（避免重复修改）
            if (exchange.getResponse().isCommitted()) {
                return;
            }

            HttpHeaders headers = exchange.getResponse().getHeaders();

            // 处理响应头中重复值的问题
            headers.entrySet().stream()
                    // 1. 过滤出有多个值的响应头
                    .filter(kv -> (kv.getValue() != null && kv.getValue().size() > 1))
                    // 2. 只处理跨域相关的响应头
                    .filter(kv -> (kv.getKey().equals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)
                            || kv.getKey().equals(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)
                            || kv.getKey().equals(HttpHeaders.VARY)))
                    .forEach(kv -> {
                        // 特殊处理 VARY 头：去重
                        if (kv.getKey().equals(HttpHeaders.VARY)) {
                            List<String> distinctValues = kv.getValue().stream()
                                    .distinct()
                                    .collect(Collectors.toList());
                            headers.put(kv.getKey(), distinctValues);
                        } else {
                            // 处理 Access-Control-Allow-Origin 和 Access-Control-Allow-Credentials
                            List<String> value = new ArrayList<>();
                            if (kv.getValue().contains(ANY)) {
                                // 如果包含 *，只保留 *
                                value.add(ANY);
                            } else {
                                // 否则只保留第一个值
                                value.add(kv.getValue().get(0));
                            }
                            headers.put(kv.getKey(), value);
                        }
                    });
        }));
    }
}