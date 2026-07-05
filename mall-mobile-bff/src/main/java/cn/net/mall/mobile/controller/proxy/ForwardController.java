package cn.net.mall.mobile.controller.proxy;

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

/**
 * 通用代理转发控制器。
 */
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

        String serviceName = null;
        String servicePath = null;
        for (Map.Entry<String, String> entry : SERVICE_MAP.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                serviceName = entry.getValue();
                servicePath = path.substring(entry.getKey().length());
                break;
            }
        }
        if (serviceName == null || servicePath == null || servicePath.isEmpty()) {
            return ResponseEntity.badRequest().body("Unknown service for path: " + path);
        }

        try {
            ServiceInstance instance = loadBalancer.choose(serviceName);
            if (instance == null) {
                return ResponseEntity.status(503).body("Service [" + serviceName + "] not available");
            }

            String targetUrl = instance.getUri() + servicePath;
            String qs = request.getQueryString();
            if (qs != null && !qs.isEmpty()) targetUrl += "?" + qs;

            HttpHeaders headers = new HttpHeaders();
            Enumeration<String> hn = request.getHeaderNames();
            while (hn.hasMoreElements()) {
                String n = hn.nextElement();
                if (!"host".equalsIgnoreCase(n) && !"content-length".equalsIgnoreCase(n)) {
                    headers.set(n, request.getHeader(n));
                }
            }

            String method = request.getMethod();
            HttpEntity entity;
            if ("GET".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
                entity = new HttpEntity<>(headers);
            } else {
                entity = new HttpEntity<>(request.getInputStream().readAllBytes(), headers);
            }

            ResponseEntity<byte[]> resp = restTemplate.exchange(
                    targetUrl, HttpMethod.valueOf(method.toUpperCase()), entity, byte[].class);
            return ResponseEntity.status(resp.getStatusCode()).headers(resp.getHeaders()).body(resp.getBody());

        } catch (Exception e) {
            log.error("Forward failed: {} {} -> {}", request.getMethod(), path, e.getMessage());
            return ResponseEntity.status(502).body("Forward failed: " + e.getMessage());
        }
    }

    private boolean shouldProxy(String path) {
        return SERVICE_PREFIXES.stream().anyMatch(path::startsWith);
    }
}
