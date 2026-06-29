package cn.net.mall.pay.controller.mobile;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.net.mall.annotation.NoLogin;
import cn.net.mall.order.dto.OrderDTO;
import cn.net.mall.pay.dto.PayWebDTO;
import cn.net.mall.pay.integration.AliPayIntegration;
import cn.net.mall.pay.service.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * 支付操作
 */
@Tag(name = "移动端-支付", description = "移动端：支付、二维码生成")
@RestController
@RequestMapping("/v1/mobile/pay")
@Validated
@RequiredArgsConstructor
public class PayController {

    private final AliPayIntegration aliPayIntegration;
    private final PayService payService;

    /**
     * 模拟支付接口
     *
     * @param payWebDTO 参数
     * @return 是否支付成功
     */
    @PostMapping("/mockPay")
    @Operation(summary = "模拟支付接口")
    public Boolean mockPay(@RequestBody @Valid PayWebDTO payWebDTO) {
        return payService.mockPay(payWebDTO);
    }

    /**
     * 支付接口
     *
     * @param orderDTO 订单实体
     * @param response 响应
     * @throws Exception
     */
    @NoLogin
    @PostMapping("/doPay")
    @Operation(summary = "支付接口")
    public void doPay(@RequestBody OrderDTO orderDTO, HttpServletResponse response) throws Exception {
        String qrUrl = aliPayIntegration.pay(orderDTO);
        QrCodeUtil.generate(qrUrl, 300, 300, "png", response.getOutputStream());
    }

    /**
     * 创建支付二维码
     *
     * @param orderDTO 订单实体
     * @return 二维码url
     * @throws Exception
     */
    @NoLogin
    @PostMapping("/createQrCode")
    @Operation(summary = "创建支付二维码")
    public String createQrCode(@RequestBody OrderDTO orderDTO) throws Exception {
        return aliPayIntegration.pay(orderDTO);
    }
}
