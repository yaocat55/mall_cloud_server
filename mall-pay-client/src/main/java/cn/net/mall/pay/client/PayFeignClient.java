package cn.net.mall.pay.client;

import cn.net.mall.pay.dto.PayCloseDTO;
import cn.net.mall.pay.dto.PayCreateDTO;
import cn.net.mall.pay.dto.PayCreateResult;
import cn.net.mall.pay.dto.PayQueryDTO;
import cn.net.mall.pay.dto.PayRefundDTO;
import cn.net.mall.pay.dto.PayRefundQueryDTO;
import cn.net.mall.pay.dto.PayRefundQueryResult;
import cn.net.mall.pay.dto.PayRefundResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 支付服务 Feign 客户端.
 *
 * <p>供 mall-order 等业务方通过 Feign 调用 mall-pay 的支付核心接口。
 * 所有接口路径对应 mall-pay 的 {@code PayInternalController}（/v1/internal/pay），
 * 走 {@code /v1/internal/**} 前缀避免被 GlobalApiResultHandler 二次包装，Feign 需要裸 DTO。</p>
 *
 * <p><b>调用方：</b>mall-order（订单服务）—— 下单后创建支付单 / 查询 / 关闭 / 退款</p>
 */
@FeignClient(value = "mall-pay-api")
public interface PayFeignClient {

    /**
     * 创建支付单（下单后调用）.
     *
     * <p>入参包含 bizOrderNo/bizType/channelCode/userId/totalAmount/subject/openId。
     * 返回 prepayParams（支付宝 orderStr / 微信 prepay_id）+ payOrderNo，前端用来拉起支付。</p>
     */
    @Operation(summary = "创建支付单", description = "由 mall-order 下单后调用，创建支付单并返回拉起参数")
    @PostMapping("/v1/internal/pay/create")
    PayCreateResult create(@RequestBody PayCreateDTO dto);

    /**
     * 查询支付状态.
     */
    @Operation(summary = "查询支付状态")
    @PostMapping("/v1/internal/pay/query")
    Object query(@RequestBody PayQueryDTO dto);

    /**
     * 关闭未支付订单.
     */
    @Operation(summary = "关闭支付")
    @PostMapping("/v1/internal/pay/close")
    Boolean close(@RequestBody PayCloseDTO dto);

    /**
     * 申请退款.
     */
    @Operation(summary = "申请退款")
    @PostMapping("/v1/internal/pay/refund/apply")
    PayRefundResult refund(@RequestBody PayRefundDTO dto);

    /**
     * 查询退款结果.
     */
    @Operation(summary = "查询退款结果")
    @PostMapping("/v1/internal/pay/refund/query")
    PayRefundQueryResult queryRefund(@RequestBody PayRefundQueryDTO dto);
}
