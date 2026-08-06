package cn.net.mall.pay.controller.internal;

import cn.net.mall.pay.dto.PayChannelDTO;
import cn.net.mall.pay.dto.PayCloseDTO;
import cn.net.mall.pay.dto.PayCreateDTO;
import cn.net.mall.pay.dto.PayCreateResult;
import cn.net.mall.pay.dto.PayQueryDTO;
import cn.net.mall.pay.entity.PayOrderEntity;
import cn.net.mall.pay.service.PayChannelService;
import cn.net.mall.pay.service.PayCoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 内部微服务接口 —— mall-order 通过 Feign 调用.
 *
 * <p>路径 {@code /v1/internal/pay} 避免被 {@code GlobalApiResultHandler} 包装，
 * 返回裸 DTO 供 Feign 消费。</p>
 *
 * <p>调用方：mall-order（创建/查询/关闭）</p>
 */
@Tag(name = "内部服务-支付", description = "内部微服务：创建/查询/关闭/渠道列表")
@RestController
@RequestMapping("/v1/internal/pay")
@RequiredArgsConstructor
public class PayInternalController {

    private final PayCoreService payCoreService;
    private final PayChannelService payChannelService;

    @Operation(summary = "查询可用支付渠道")
    @GetMapping("/channels")
    public List<PayChannelDTO> channels() {
        return payChannelService.listAvailableChannels();
    }

    @Operation(summary = "创建支付单")
    @PostMapping("/create")
    public PayCreateResult create(@RequestBody PayCreateDTO dto) {
        return payCoreService.create(dto);
    }

    @Operation(summary = "查询支付状态")
    @PostMapping("/query")
    public PayOrderEntity query(@RequestBody PayQueryDTO dto) {
        return payCoreService.query(dto);
    }

    @Operation(summary = "关闭支付")
    @PostMapping("/close")
    public Boolean close(@RequestBody PayCloseDTO dto) {
        return payCoreService.close(dto.getPayOrderNo(), dto.getReason());
    }
}
