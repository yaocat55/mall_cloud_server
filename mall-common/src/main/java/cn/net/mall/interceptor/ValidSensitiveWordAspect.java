package cn.net.mall.interceptor;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.net.mall.annotation.ValidSensitiveWordField;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.util.TokenUtil;
import cn.net.mall.json.JacksonMapper;
import cn.net.mall.util.ApiResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 校验敏感词拦截器
 *
 * @date 2024/5/20 下午3:33
 */
@Slf4j
@Aspect
@Component
@ConditionalOnClass(name = "org.aspectj.lang.ProceedingJoinPoint")
public class ValidSensitiveWordAspect {

    @Value("${mall.basic.sensitiveCheckUrl:http://mall-basic-api/v1/commonSensitiveWord/checkSensitiveWord}")
    private String sensitiveCheckUrl;
    @Value("${mall.basic.host:}")
    private String basicHost;
    @Value("${mall.gateway.host:http://localhost:8020}")
    private String gatewayHost;
    @Value("${mall.basic.sensitiveCheckTimeoutMs:1500}")
    private int sensitiveCheckTimeoutMs;
    @Value("${mall.basic.sensitiveCheckCacheTtlSeconds:60}")
    private int sensitiveCheckCacheTtlSeconds;
    @Value("${mall.basic.sensitiveCheckCacheMaxSize:2048}")
    private int sensitiveCheckCacheMaxSize;
    @Value("${mall.basic.sensitiveCheckMaxConcurrency:4}")
    private int sensitiveCheckMaxConcurrency;

    private ExecutorService sensitiveCheckExecutor;

    private final LocalTtlCache localTtlCache = new LocalTtlCache();

    @PostConstruct
    public void init() {
        int nThreads = Math.max(1, Math.min(8, sensitiveCheckMaxConcurrency));
        AtomicInteger idx = new AtomicInteger(1);
        ThreadFactory tf = r -> {
            Thread t = new Thread(r);
            t.setName("sensitive-check-" + idx.getAndIncrement());
            t.setDaemon(true);
            return t;
        };
        this.sensitiveCheckExecutor = Executors.newFixedThreadPool(nThreads, tf);
    }

    @PreDestroy
    public void destroy() {
        ExecutorService es = this.sensitiveCheckExecutor;
        if (es != null) {
            es.shutdown();
        }
    }

    @Pointcut("@annotation(cn.net.mall.annotation.ValidSensitiveWord)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        List<String> content = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        // 获取参数
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            Object value = args[i];
            if (value instanceof String) {
                content.add((String) value);
            } else {
                parseEntity(value, content, visited);
            }
        }

        validSensitiveWord(content);
        return joinPoint.proceed();
    }

    private void validSensitiveWord(List<String> content) {
        if (content == null || content.isEmpty()) {
            return;
        }

        String collectText = normalizeText(content);
        if (!StringUtils.hasText(collectText)) {
            return;
        }
        if (localTtlCache.hit(collectText)) {
            return;
        }
        callRemoteCheck(collectText);
        localTtlCache.put(collectText, sensitiveCheckCacheTtlSeconds, sensitiveCheckCacheMaxSize);
    }


    private void parseEntity(Object object, List<String> content, Set<Integer> visited) throws IllegalAccessException {
        if (object == null) {
            return;
        }
        int id = System.identityHashCode(object);
        if (visited.contains(id)) {
            return;
        }
        visited.add(id);

        if (object instanceof List) {
            List<?> list = (List<?>) object;
            for (Object obj : list) {
                parseEntity(obj, content, visited);
            }
            return;
        }
        if (object instanceof Set) {
            Set<?> set = (Set<?>) object;
            for (Object obj : set) {
                parseEntity(obj, content, visited);
            }
            return;
        }
        if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) object;
            for (Object v : map.values()) {
                parseEntity(v, content, visited);
            }
            return;
        }
        Class<?> clazz = object.getClass();
        if (clazz.isArray()) {
            Object[] arr = (Object[]) object;
            for (Object v : arr) {
                parseEntity(v, content, visited);
            }
            return;
        }
        if (isSimpleType(clazz)) {
            return;
        }

        List<Field> fields = new ArrayList<>();
        Class<?> aClass = object.getClass();
        while (Objects.nonNull(aClass)) {
            fields.addAll(Arrays.asList(aClass.getDeclaredFields()));
            aClass = aClass.getSuperclass();
        }
        if (fields.isEmpty()) {
            return;
        }

        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(object);
            if (Objects.isNull(value)) {
                continue;
            }
            if (field.isAnnotationPresent(ValidSensitiveWordField.class)) {
                if (value instanceof String) {
                    content.add((String) value);
                } else {
                    parseEntity(value, content, visited);
                }
            } else {
                parseEntity(value, content, visited);
            }
        }
    }

    private void callRemoteCheck(String text) {
        List<String> urls = candidateUrls();
        String body = "{\"word\":\"" + text.replace("\"", "\\\"") + "\"}";
        String authorization = getAuthorizationFromContext();
        if (urls.isEmpty()) {
            throw new BusinessException(500, "SensitiveWordCheck endpoints empty");
        }

        List<String> sortedUrls = urls.stream()
                .sorted(Comparator.comparingInt(this::endpointPriority))
                .collect(Collectors.toList());

        AtomicInteger remaining = new AtomicInteger(sortedUrls.size());
        AtomicReference<BusinessException> businessExceptionRef = new AtomicReference<>();
        AtomicReference<RuntimeException> lastRuntimeExceptionRef = new AtomicReference<>();

        CompletableFuture<Void> finished = new CompletableFuture<>();

        for (String url : sortedUrls) {
            CompletableFuture.runAsync(() -> doRemoteCheck(url, body, authorization), sensitiveCheckExecutor)
                    .whenComplete((v, ex) -> {
                        if (finished.isDone()) {
                            remaining.decrementAndGet();
                            return;
                        }
                        if (ex == null) {
                            finished.complete(null);
                            remaining.decrementAndGet();
                            return;
                        }
                        RuntimeException e = unwrap(ex);
                        if (e instanceof BusinessException) {
                            businessExceptionRef.compareAndSet(null, (BusinessException) e);
                            finished.completeExceptionally(e);
                            remaining.decrementAndGet();
                            return;
                        }
                        lastRuntimeExceptionRef.set(e);
                        if (remaining.decrementAndGet() == 0) {
                            BusinessException be = businessExceptionRef.get();
                            if (be != null) {
                                finished.completeExceptionally(be);
                                return;
                            }
                            RuntimeException last = lastRuntimeExceptionRef.get();
                            String msg = last != null && last.getMessage() != null ? last.getMessage() : "SensitiveWordCheck all endpoints failed";
                            finished.completeExceptionally(new BusinessException("SensitiveWordCheck error: " + msg));
                        }
                    });
        }

        try {
            finished.get(Math.max(1, sensitiveCheckTimeoutMs) + 500L, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            RuntimeException ex = unwrap(e);
            if (ex instanceof BusinessException) {
                throw ex;
            }
            throw new BusinessException("SensitiveWordCheck error: " + (ex.getMessage() == null ? ex.toString() : ex.getMessage()));
        }
    }

    private List<String> candidateUrls() {
        List<String> list = new ArrayList<>();
        if (sensitiveCheckUrl != null && !sensitiveCheckUrl.isEmpty()) {
            list.add(sensitiveCheckUrl);
        }
        if (basicHost != null && !basicHost.isEmpty()) {
            list.add(basicHost.endsWith("/") ? basicHost + "v1/commonSensitiveWord/checkSensitiveWord"
                    : basicHost + "/v1/commonSensitiveWord/checkSensitiveWord");
        }
        list.add("http://mall-basic-api/v1/commonSensitiveWord/checkSensitiveWord");
        if (gatewayHost != null && !gatewayHost.isEmpty()) {
            list.add(gatewayHost.endsWith("/") ? gatewayHost + "api/basic/v1/commonSensitiveWord/checkSensitiveWord"
                    : gatewayHost + "/api/basic/v1/commonSensitiveWord/checkSensitiveWord");
        }
        return list;
    }

    private int endpointPriority(String url) {
        if (url == null) {
            return 100;
        }
        if (url.startsWith("http://mall-basic-api") || url.startsWith("https://mall-basic-api")) {
            return 0;
        }
        if (url.contains("/v1/commonSensitiveWord/checkSensitiveWord") && !url.contains("/api/basic/")) {
            return 1;
        }
        if (isGatewayUrl(url)) {
            return 2;
        }
        return 10;
    }

    private void doRemoteCheck(String url, String body, String authorization) {
        long start = System.currentTimeMillis();
        try {
            HttpRequest req = HttpRequest.post(url)
                    .contentType("application/json")
                    .body(body)
                    .timeout(Math.max(1, sensitiveCheckTimeoutMs));
            if (!isGatewayUrl(url)) {
                req.header("INNER-REQUEST", "true");
            }
            if (StringUtils.hasText(authorization)) {
                req.header(TokenUtil.AUTHORIZATION, authorization);
            }
            try (HttpResponse response = req.execute()) {
                int status = response.getStatus();
                String resp = new String(response.bodyBytes(), StandardCharsets.UTF_8);
                if (status >= 400) {
                    String parsedMsg = parseMessage(resp);
                    throw new RuntimeException("SensitiveWordCheck http status=" + status + ", url=" + url + ", msg=" + (parsedMsg != null ? parsedMsg : truncate(resp, 256)));
                }
                int bodyCode = 200;
                String bodyMsg = null;
                Integer c = parseBodyCode(resp);
                if (c != null) {
                    bodyCode = c;
                    bodyMsg = parseBodyMessage(resp);
                }
                if (bodyCode != 200) {
                    throw new BusinessException(bodyCode, bodyMsg != null ? bodyMsg : resp);
                }
            }
        } catch (RuntimeException e) {
            long cost = System.currentTimeMillis() - start;
            log.warn("SensitiveWordCheck failed cost={}ms, url={}, error={}", cost, url, e.toString());
            throw e;
        }
    }

    private RuntimeException unwrap(Throwable ex) {
        Throwable cur = ex;
        while (cur != null) {
            if (cur instanceof RuntimeException) {
                return (RuntimeException) cur;
            }
            cur = cur.getCause();
        }
        return new RuntimeException(ex);
    }

    private String normalizeText(List<String> content) {
        List<String> cleaned = content.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());
        if (cleaned.isEmpty()) {
            return "";
        }
        String joined = String.join(",", cleaned);
        int maxLen = 5000;
        if (joined.length() <= maxLen) {
            return joined;
        }
        return joined.substring(0, maxLen);
    }

    private static class LocalTtlCache {
        private final ConcurrentHashMap<String, Long> expireAtMillis = new ConcurrentHashMap<>();
        private final ConcurrentLinkedDeque<String> order = new ConcurrentLinkedDeque<>();

        boolean hit(String text) {
            Long expireAt = expireAtMillis.get(text);
            if (expireAt == null) {
                return false;
            }
            if (System.currentTimeMillis() <= expireAt) {
                return true;
            }
            expireAtMillis.remove(text, expireAt);
            return false;
        }

        void put(String text, int ttlSeconds, int maxSize) {
            if (!StringUtils.hasText(text)) {
                return;
            }
            int ttl = Math.max(1, ttlSeconds);
            expireAtMillis.put(text, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(ttl));
            order.addLast(text);
            evict(maxSize);
        }

        private void evict(int maxSize) {
            int limit = Math.max(128, maxSize);
            while (expireAtMillis.size() > limit) {
                String k = order.pollFirst();
                if (k == null) {
                    return;
                }
                expireAtMillis.remove(k);
            }
        }
    }

    private boolean isSimpleType(Class<?> clazz) {
        if (CharSequence.class.isAssignableFrom(clazz)) {
            return true;
        }
        if (Number.class.isAssignableFrom(clazz)) {
            return true;
        }
        if (Boolean.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz)) {
            return true;
        }
        if (Enum.class.isAssignableFrom(clazz)) {
            return true;
        }
        String pkg = clazz.getPackage() != null ? clazz.getPackage().getName() : "";
        return pkg.startsWith("java.") || pkg.startsWith("javax.") || pkg.startsWith("jakarta.");
    }

    private String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max) + "...";
    }

    private String getAuthorizationFromContext() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                return null;
            }
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            return TokenUtil.getAuthorization(request);
        } catch (Throwable ignore) {
            return null;
        }
    }

    private boolean isGatewayUrl(String url) {
        return url != null && url.contains("/api/basic/");
    }

    private String parseMessage(String resp) {
        if (resp == null || resp.isEmpty()) {
            return null;
        }
        try {
            JacksonMapper mapper = new JacksonMapper();
            ApiResult<?> ar = mapper.readValue(resp, ApiResult.class);
            if (ar != null && ar.getMessage() != null && !ar.getMessage().isEmpty()) {
                return ar.getMessage();
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    private Integer parseBodyCode(String resp) {
        try {
            JSONObject obj = JSON.parseObject(resp);
            if (obj == null) {
                return null;
            }
            Integer code = obj.getInteger("code");
            if (code != null) {
                return code;
            }
            JSONObject data = obj.getJSONObject("data");
            if (data != null) {
                Integer inner = data.getInteger("code");
                if (inner != null) {
                    return inner;
                }
            }
        } catch (Exception ignore) {}
        return null;
    }

    private String parseBodyMessage(String resp) {
        try {
            JSONObject obj = JSON.parseObject(resp);
            if (obj == null) {
                return null;
            }
            String msg = obj.getString("message");
            if (msg != null && !msg.isEmpty()) {
                return msg;
            }
            JSONObject data = obj.getJSONObject("data");
            if (data != null) {
                String innerMsg = data.getString("message");
                if (innerMsg != null && !innerMsg.isEmpty()) {
                    return innerMsg;
                }
            }
        } catch (Exception ignore) {}
        return null;
    }
}
