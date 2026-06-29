package cn.net.mall.pay.client;

import cn.net.mall.order.dto.OrderDTO;
import cn.net.mall.pay.dto.PayWebDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 支付服务 Feign 客户端
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>mall-order（订单服务）— 支付时调用模拟支付接口</li>
 *   <li>mall-mobile-api（BFF 服务）— 创建支付二维码</li>
 * </ul>
 */
@FeignClient(value = "susan-mall-pay")
public interface PayFeignClient {

    /**
     * 模拟支付接口
     * <p>调用方：mall-order（订单服务）</p>
     */
    @Operation(summary = "模拟支付接口", description = "由 mall-order 调用，模拟支付流程")
    @PostMapping("/v1/mobile/pay/mockPay")
    Boolean mockPay(@RequestBody PayWebDTO payWebDTO);

    /**
     * 创建支付二维码
     * <p>调用方：mall-mobile-api（BFF 服务）</p>
     */
    @Operation(summary = "创建支付二维码", description = "由 mall-mobile-api(BFF) 调用，创建支付二维码")
    @PostMapping("/v1/mobile/pay/createQrCode")
    String createQrCode(@RequestBody OrderDTO orderDTO);
}
