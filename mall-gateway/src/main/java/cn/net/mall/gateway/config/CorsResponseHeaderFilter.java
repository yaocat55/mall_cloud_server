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

@Component
public class CorsResponseHeaderFilter implements GlobalFilter, Ordered {


    private static final String ANY = "*";

    @Override
    public int getOrder() {
        // 指定此过滤器位于NettyWriteResponseFilter之后
        // 即待处理完响应体后接着处理响应头
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER + 1;
    }

    @Override
    @SuppressWarnings("serial")
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            if (exchange.getResponse().isCommitted()) {
                return;
            }
            HttpHeaders headers = exchange.getResponse().getHeaders();
            headers.entrySet().stream()
                    .filter(kv -> (kv.getValue() != null && kv.getValue().size() > 1))
                    .filter(kv -> (kv.getKey().equals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)
                            || kv.getKey().equals(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)
                            || kv.getKey().equals(HttpHeaders.VARY)))
                    .forEach(kv -> {
                        if (kv.getKey().equals(HttpHeaders.VARY)) {
                            headers.put(kv.getKey(), kv.getValue().stream().distinct().collect(Collectors.toList()));
                        } else {
                            List<String> value = new ArrayList<>();
                            if (kv.getValue().contains(ANY)) {
                                value.add(ANY);
                            } else {
                                value.add(kv.getValue().get(0));
                            }
                            headers.put(kv.getKey(), value);
                        }
                    });
        }));
    }
}
