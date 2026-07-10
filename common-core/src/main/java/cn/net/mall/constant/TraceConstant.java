package cn.net.mall.constant;

/**
 * @date 2025/7/4 19:03
 */
public class TraceConstant {

    /**
     * MDC 中 TraceId 的 key
     */
    public static final String TRACE_ID = "tid";

    /**
     * Reactor Context 中 TraceId 的 key
     */
    public static final String REACTOR_TRACE_ID = "REACTOR_TRACE_ID";

    /**
     * HTTP Header 中 TraceId 的 key
     */
    public static final String HEADER_TRACE_ID = "X-Trace-Id";
}