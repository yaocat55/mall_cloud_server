package cn.net.mall.gateway.util;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;
import reactor.util.context.Context;

import java.util.UUID;

import static cn.net.mall.constant.TraceConstant.REACTOR_TRACE_ID;
import static cn.net.mall.constant.TraceConstant.TRACE_ID;

/**
 * TraceId 工具类
 */
public class TraceIdUtil {

    /**
     * 获取或生成 TraceId
     * 优先从 SkyWalking 获取，如果获取不到则生成 UUID
     */
    public static String getOrGenerateTraceId() {
        String traceId = TraceContext.traceId();
        if (isValidTraceId(traceId)) {
            return traceId;
        }
        return generateTraceId();
    }

    /**
     * 生成新的 TraceId
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 检查 TraceId 是否有效
     */
    public static boolean isValidTraceId(String traceId) {
        return traceId != null &&
                !traceId.isEmpty() &&
                !"N/A".equals(traceId) &&
                !"Ignored_Trace".equals(traceId);
    }

    /**
     * 设置 MDC 中的 TraceId
     */
    public static void setMDCTraceId(String traceId) {
        if (traceId != null) {
            MDC.put(TRACE_ID, traceId);
        }
    }

    /**
     * 获取 MDC 中的 TraceId
     */
    public static String getMDCTraceId() {
        return MDC.get(TRACE_ID);
    }

    /**
     * 清除 MDC 中的 TraceId
     */
    public static void clearMDCTraceId() {
        MDC.remove(TRACE_ID);
    }

    /**
     * 从 Reactor Context 中获取 TraceId
     */
    public static String getTraceIdFromContext(Context context) {
        if (context.hasKey(REACTOR_TRACE_ID)) {
            return context.get(REACTOR_TRACE_ID);
        }
        return null;
    }
}
