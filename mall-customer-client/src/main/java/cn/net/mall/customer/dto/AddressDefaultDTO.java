package cn.net.mall.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 设置默认收货地址 DTO
 */
@Schema(description = "设置默认收货地址 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddressDefaultDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "地址ID不能为空")
    @Schema(description = "地址ID", example = "1")
    private Long id;
}
