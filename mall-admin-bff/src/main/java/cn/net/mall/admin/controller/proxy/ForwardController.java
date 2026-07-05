package cn.net.mall.admin.controller.proxy;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Hidden
public class ForwardController {

    private final RestTemplate restTemplate;
    private final LoadBalancerClient loadBalancer;

    private static final Set<String> SERVICE_PREFIXES = Set.of(
            "/auth", "/basic", "/product", "/marketing",
            "/order", "/pay", "/recommend", "/message"
    );

    private static final Map<String, String> SERVICE_MAP = new LinkedHashMap<>();
    static {
        SERVICE_MAP.put("/auth", "mall-auth-api");
        SERVICE_MAP.put("/basic", "mall-basic-api");
        SERVICE_MAP.put("/product", "mall-product-api");
        SERVICE_MAP.put("/marketing", "mall-marketing-api");
        SERVICE_MAP.put("/order", "mall-order-api");
        SERVICE_MAP.put("/pay", "mall-pay-api");
        SERVICE_MAP.put("/recommend", "mall-recommend-api");
        SERVICE_MAP.put("/message", "mall-message-api");
    }

    @RequestMapping(path = {"/auth/**", "/basic/**", "/product/**", "/marketing/**",
            "/order/**", "/pay/**", "/recommend/**", "/message/**"})
    public Object forward(HttpServletRequest request) {
        String path = request.getRequestURI();

        String serviceName = null, servicePath = null;
        for (Map.Entry<String, String> e : SERVICE_MAP.entrySet()) {
            if (path.startsWith(e.getKey())) {
                serviceName = e.getValue();
                servicePath = path.substring(e.getKey().length());
                break;
            }
        }
        if (serviceName == null) {
            return ResponseEntity.badRequest().body("Unknown service for: " + path);
        }

        try {
            ServiceInstance instance = loadBalancer.choose(serviceName);
            if (instance == null) {
                return ResponseEntity.status(503).body("Service " + serviceName + " unavailable");
            }

            String url = instance.getUri() + servicePath;
            String qs = request.getQueryString();
            if (qs != null) url += "?" + qs;

            HttpHeaders hd = new HttpHeaders();
            Enumeration<String> hn = request.getHeaderNames();
            while (hn.hasMoreElements()) {
                String n = hn.nextElement();
                if (!"host".equalsIgnoreCase(n) && !"content-length".equalsIgnoreCase(n)) {
                    hd.set(n, request.getHeader(n));
                }
            }

            String m = request.getMethod();
            HttpEntity entity = "GET".equalsIgnoreCase(m) || "DELETE".equalsIgnoreCase(m)
                    ? new HttpEntity<>(hd)
                    : new HttpEntity<>(request.getInputStream().readAllBytes(), hd);

            ResponseEntity resp = restTemplate.exchange(
                    url, HttpMethod.valueOf(m.toUpperCase()), entity, byte[].class);
            return ResponseEntity.status(resp.getStatusCode()).headers(resp.getHeaders()).body(resp.getBody());

        } catch (Exception e) {
            log.error("Proxy failed: {} {} -> {}", request.getMethod(), path, e.getMessage());
            return ResponseEntity.status(502).body("Proxy error: " + e.getMessage());
        }
    }

    private boolean shouldProxy(String path) {
        return SERVICE_PREFIXES.stream().anyMatch(path::startsWith);
    }
}
