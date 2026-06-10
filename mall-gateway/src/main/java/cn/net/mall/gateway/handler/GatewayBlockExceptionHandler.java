package cn.net.mall.gateway.handler;


import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.nacos.shaded.com.google.common.collect.Maps;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GatewayBlockExceptionHandler {

    @ExceptionHandler(FlowException.class)
    public Map<String, Object> handlerFlowException() {
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("code", HttpStatus.TOO_MANY_REQUESTS.value());
        map.put("message", "请求太频繁了，请稍后重试");
        return map;
    }

    @ExceptionHandler(DegradeException.class)
    public Map<String, Object> handlerDegradeException() {
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("code", HttpStatus.TOO_MANY_REQUESTS.value());
        map.put("message", "请求被熔断了，请稍后重试");
        return map;
    }

    @ExceptionHandler(ParamFlowException.class)
    public Map<String, Object> handlerParamFlowException() {
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("code", HttpStatus.TOO_MANY_REQUESTS.value());
        map.put("message", "请求太频繁了，请稍后重试");
        return map;
    }

    @ExceptionHandler(AuthorityException.class)
    public Map<String, Object> handlerAuthorityException() {
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("code", HttpStatus.UNAUTHORIZED.value());
        map.put("message", "你没有访问权限，请稍后重试");
        return map;
    }
}
