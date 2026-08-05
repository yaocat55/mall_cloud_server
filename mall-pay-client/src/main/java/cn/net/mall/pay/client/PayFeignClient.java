package cn.net.mall.pay.client;

import cn.net.mall.pay.dto.PayCloseDTO;
import cn.net.mall.pay.dto.PayCreateDTO;
import cn.net.mall.pay.dto.PayCreateResult;
import cn.net.mall.pay.dto.PayChannelDTO;
import cn.net.mall.pay.dto.PayQueryDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 支付服务 Feign 客户端.
 *
 * <p>供 mall-order 等业务方通过 Feign 调用 mall-pay 的内部接口。
 * 内部编排走 create/close，前端 C 端渠道列表走 channels。</p>
 */
@FeignClient(value = "mall-pay-api")
public interface PayFeignClient {

    /**
     * 查询可用支付渠道（供 BFF 聚合给前端）.
     */
    @Operation(summary = "查询可用支付渠道")
    @GetMapping("/v1/internal/pay/channels")
    List<PayChannelDTO> channels();

    /**
     * 创建支付单（下单后调用）.
     */
    @Operation(summary = "创建支付单")
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
}
