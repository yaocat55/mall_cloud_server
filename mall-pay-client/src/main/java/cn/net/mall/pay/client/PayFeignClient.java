package cn.net.mall.pay.client;

import cn.net.mall.order.dto.OrderDTO;
import cn.net.mall.pay.dto.PayWebDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 支付服务客户端
 */
@FeignClient(value = "susan-mall-pay")
public interface PayFeignClient {

    /**
     * 模拟支付接口
     *
     * @param payWebDTO 参数
     * @return 是否支付成功
     */
    @PostMapping("/v1/mobile/pay/mockPay")
    Boolean mockPay(@RequestBody PayWebDTO payWebDTO);

    /**
     * 创建支付二维码
     *
     * @param orderDTO 订单实体
     * @return 二维码url
     */
    @PostMapping("/v1/mobile/pay/createQrCode")
    String createQrCode(@RequestBody OrderDTO orderDTO);
}
