package cn.net.mall.auth.controller;

import cn.net.mall.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.net.mall.auth.filter.JwtTokenFilter.*;


/**
 * filer异常处理
 *
 * @date 2024/9/28 下午5:04
 */
@Slf4j
@RestController
public class FilterExceptionController {

    @RequestMapping(FILTER_ERROR_PATH)
    public void handleException(HttpServletRequest request) {
        Object exception = request.getAttribute(FILTER_ERROR);
        if (exception instanceof BusinessException) {
            BusinessException businessException = (BusinessException) exception;
            throw businessException;
        }
        throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误，请联系系统管理员！");
    }
}
