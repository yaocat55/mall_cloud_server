package cn.net.mall.feign;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Feign 客户端降级代理工厂.
 *
 * <p>通过 JDK 动态代理为 FeignClient 接口生成降级实现，根据方法返回类型自动推断兜底值：
 * List→空列表、boolean→false、int/long→0、DTO→null 等。</p>
 *
 * <p>替代传统 FallbackFactory 中逐方法编写 return 模板代码的模式：</p>
 * <pre>{@code
 * // 改造前：14 个方法逐个写 return
 * // 改造后：
 * @Component
 * public class XxxFallbackFactory implements FallbackFactory<XxxFeignClient> {
 *     public XxxFeignClient create(Throwable cause) {
 *         return FeignFallbackProxy.create(XxxFeignClient.class, cause);
 *     }
 * }
 * }</pre>
 */
@Slf4j
public class FeignFallbackProxy implements InvocationHandler {

    private static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS = Map.of(
            boolean.class, false,
            byte.class, (byte) 0,
            short.class, (short) 0,
            int.class, 0,
            long.class, 0L,
            float.class, 0f,
            double.class, 0d,
            char.class, '\0',
            void.class, null
    );

    private final Class<?> interfaceType;

    private FeignFallbackProxy(Class<?> interfaceType) {
        this.interfaceType = interfaceType;
    }

    /**
     * 创建 FeignClient 接口的降级代理实例.
     *
     * @param <T>           FeignClient 接口类型
     * @param interfaceType FeignClient 接口 class
     * @param cause         导致降级的异常
     * @return 代理实例，调用任何方法返回安全默认值
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> interfaceType, Throwable cause) {
        if (log.isDebugEnabled()) {
            log.debug("创建降级代理: {} (cause: {})", interfaceType.getSimpleName(), cause.getMessage());
        }
        return (T) Proxy.newProxyInstance(
                interfaceType.getClassLoader(),
                new Class<?>[]{interfaceType},
                new FeignFallbackProxy(interfaceType)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Object 通用方法直接调用
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        log.warn("[降级] {}.{} 返回默认值", interfaceType.getSimpleName(), method.getName());

        Class<?> returnType = method.getReturnType();
        return getDefaultValue(returnType);
    }

    /**
     * 根据方法的返回类型选择默认安全值.
     */
    private Object getDefaultValue(Class<?> type) {
        if (type.isPrimitive()) {
            return PRIMITIVE_DEFAULTS.get(type);
        }
        if (type == Boolean.class) return Boolean.FALSE;
        if (type == Integer.class) return 0;
        if (type == Long.class) return 0L;
        if (type == Double.class) return 0d;
        if (type == String.class) return "";
        if (Collection.class.isAssignableFrom(type)) return Collections.emptyList();
        if (Map.class.isAssignableFrom(type)) return Collections.emptyMap();
        return null;
    }
}
