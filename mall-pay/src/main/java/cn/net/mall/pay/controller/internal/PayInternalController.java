package cn.net.mall.pay.controller.internal;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "内部服务-支付", description = "内部微服务：mall-order 通过 Feign 调用（预留）")
@RestController
@RequestMapping("/v1/internal/pay")
public class PayInternalController {
    // 预留：纯内部支付接口（create/query/close/refund 等，Feign 调用返回裸 DTO，避免被 GlobalApiResultHandler 包装）
}
