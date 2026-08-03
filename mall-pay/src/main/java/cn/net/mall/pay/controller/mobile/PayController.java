package cn.net.mall.pay.controller.mobile;

import cn.net.mall.pay.dto.PayCloseDTO;
import cn.net.mall.pay.dto.PayCreateDTO;
import cn.net.mall.pay.dto.PayCreateResult;
import cn.net.mall.pay.dto.PayQueryDTO;
import cn.net.mall.pay.dto.PayRefundDTO;
import cn.net.mall.pay.dto.PayRefundQueryDTO;
import cn.net.mall.pay.dto.PayRefundResult;
import cn.net.mall.pay.dto.PayRefundQueryResult;
import cn.net.mall.pay.entity.PayOrderEntity;
import cn.net.mall.pay.service.PayCoreService;
import cn.net.mall.pay.service.RefundCoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 移动端支付收银台（C 端专属）.
 *
 * <p>前端/移动端支付链路：前端 → 网关 → mall-order（下单）→ Feign 调 mall-pay 创建支付单 →
 * 返回拉起支付参数 → 前端调起渠道支付。支付结果经渠道回调 + MQ 通知业务方。</p>
 * <p>本控制器面向 C 端用户收银台（创建支付单、查状态、关单、退款申请）。</p>
 * <p>返回值由 common-web 的 {@code GlobalApiResultHandler} 自动包装为 ApiResult（/v1/** 前缀），无需手动包裹。</p>
 */
@Tag(name = "移动端-支付收银台", description = "移动端：创建支付单、查询支付状态、关闭支付、申请退款")
@RestController
@RequestMapping("/v1/mobile/pay")
@Validated
@RequiredArgsConstructor
public class PayController {

    private final PayCoreService payCoreService;
    private final RefundCoreService refundCoreService;

    @Operation(summary = "创建支付单", description = "收银台调起支付前创建支付单，返回拉起支付参数（支付宝 orderStr / 微信 prepay / MOCK 预设串）")
    @PostMapping("/create")
    public PayCreateResult create(@RequestBody @Valid PayCreateDTO dto) {
        return payCoreService.create(dto);
    }

    @Operation(summary = "查询支付状态", description = "按 payOrderNo 或 bizOrderNo 查询支付单当前状态")
    @PostMapping("/query")
    public PayOrderEntity query(@RequestBody @Valid PayQueryDTO dto) {
        return payCoreService.query(dto);
    }

    @Operation(summary = "关闭支付", description = "关闭未支付订单（用户取消/超时），释放占用")
    @PostMapping("/close")
    public Boolean close(@RequestBody @Valid PayCloseDTO dto) {
        return payCoreService.close(dto.getPayOrderNo(), dto.getReason());
    }

    @Operation(summary = "申请退款", description = "售后退款申请，自动退款或进入人工审核")
    @PostMapping("/refund/apply")
    public PayRefundResult applyRefund(@RequestBody @Valid PayRefundDTO dto) {
        return refundCoreService.apply(dto);
    }

    @Operation(summary = "查询退款结果", description = "按退款单号查询退款状态")
    @PostMapping("/refund/query")
    public PayRefundQueryResult queryRefund(@RequestBody @Valid PayRefundQueryDTO dto) {
        return refundCoreService.queryRefund(dto.getRefundNo());
    }
}
