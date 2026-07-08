package cn.net.mall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 签名实体
 *
 * @date 2024/7/28 上午11:23
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignEntity implements Serializable {

    /**
     * 时间戳
     */
    @Schema(description = "timestamp", example = "0")
    private Long timestamp;

    /**
     * 签名
     */
    @Schema(description = "sign", example = "string")
    private String sign;
}
