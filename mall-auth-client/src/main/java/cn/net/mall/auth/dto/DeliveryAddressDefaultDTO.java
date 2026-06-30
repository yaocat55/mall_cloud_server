package cn.net.mall.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 收货地址默认设置 DTO
 */
@Schema(description = "收货地址默认设置 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeliveryAddressDefaultDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 系统ID
     */
    @NotNull(message = "系统ID不能为空")
    @Schema(description = "系统ID")
    private Long id;
}
