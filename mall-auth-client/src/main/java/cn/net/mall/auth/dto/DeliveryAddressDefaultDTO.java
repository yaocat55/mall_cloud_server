package cn.net.mall.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 收货地址默认设置DTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeliveryAddressDefaultDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 系统ID
     */
    @NotNull(message = "系统ID不能为空")
    private Long id;
}
