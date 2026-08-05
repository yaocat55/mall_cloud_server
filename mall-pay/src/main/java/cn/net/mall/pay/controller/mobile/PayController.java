package cn.net.mall.pay.controller.mobile;

import cn.net.mall.pay.dto.PayChannelDTO;
import cn.net.mall.pay.dto.PayCreateDTO;
import cn.net.mall.pay.dto.PayCreateResult;
import cn.net.mall.pay.dto.PayQueryDTO;
import cn.net.mall.pay.entity.PayOrderEntity;
import cn.net.mall.pay.service.PayChannelService;
import cn.net.mall.pay.service.PayCoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 移动端支付收银台（C 端专属）.
 *
 * <p>前端支付链路：选择支付渠道 → 下单 → 调 /pay/create 拿拉起参数 → 拉起 SDK → 轮询 /pay/query。</p>
 * <p>返回值由 common-web 的 GlobalApiResultHandler 自动包装为 ApiResult（/v1/** 前缀），无需手动包裹。</p>
 */
@Tag(name = "移动端-支付收银台", description = "移动端：渠道列表、创建支付单、查询支付状态")
@RestController
@RequestMapping("/v1/mobile/pay")
@Validated
@RequiredArgsConstructor
public class PayController {

    private final PayCoreService payCoreService;
    private final PayChannelService payChannelService;

    @Operation(summary = "查询可用支付渠道", description = "返回启用中的支付渠道列表，前端收银台渲染支付方式")
    @GetMapping("/channels")
    public List<PayChannelDTO> channels() {
        return payChannelService.listAvailableChannels();
    }

    @Operation(summary = "创建支付单", description = "调起支付前创建支付单，返回拉起支付参数")
    @PostMapping("/create")
    public PayCreateResult create(@RequestBody @Valid PayCreateDTO dto) {
        return payCoreService.create(dto);
    }

    @Operation(summary = "查询支付状态", description = "按 payOrderNo 或 bizOrderNo 查询支付单当前状态")
    @PostMapping("/query")
    public PayOrderEntity query(@RequestBody @Valid PayQueryDTO dto) {
        return payCoreService.query(dto);
    }
}
