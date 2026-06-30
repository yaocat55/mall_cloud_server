package cn.net.mall.auth.entity.shopping.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 收货地址实体
 *
 * @date 2024-09-10 10:02:01
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "默认收货地址请求")

public class DeliveryAddressDefaultWebEntity {

    /**
     * 系统ID
     */
    @NotNull(message = "系统ID不能为空")
    private Long id;
}
