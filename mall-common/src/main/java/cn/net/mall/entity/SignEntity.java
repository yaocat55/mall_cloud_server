package cn.net.mall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
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
    private Long timestamp;

    /**
     * 签名
     */
    private String sign;
}
