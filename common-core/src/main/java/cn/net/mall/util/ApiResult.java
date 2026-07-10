package cn.net.mall.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 请求响应实体
 *
 * @date 2024/1/9 下午1:20
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "统一 API 响应格式")
public class ApiResult<T> {

    /**
     * 请求成功状态码
     */
    public static final int OK = 200;

    /**
     * 接口返回码
     */
    @Schema(description = "状态码", example = "200")
    private int code;

    /**
     * 接口返回信息
     */
    @Schema(description = "提示信息", example = "操作成功")
    private String message;

    /**
     * 数据
     */
    @Schema(description = "数据")
    private T data;
}
