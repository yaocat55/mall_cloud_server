package cn.net.mall.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常
 *
 * @date 2024/1/9 下午1:12
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class BusinessException extends RuntimeException {

    public static final long serialVersionUID = -6735897190745766939L;

    /**
     * 内部服务器错误默认状态码
     */
    private static final int INTERNAL_SERVER_ERROR_CODE = 500;

    /**
     * 异常码
     */
    private int code;

    /**
     * 具体异常信息
     */
    private String message;

    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        this.code = INTERNAL_SERVER_ERROR_CODE;
        this.message = message;
    }
}
