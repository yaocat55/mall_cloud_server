package cn.net.mall.pay.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.pay.dto.RowsDTO;
import cn.net.mall.pay.entity.PayChannelConfigEntity;
import cn.net.mall.pay.entity.PayChannelConfigConditionEntity;
import cn.net.mall.pay.service.PayChannelConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 支付渠道配置 接口层（管理端）
 */
@Tag(name = "支付渠道配置", description = "支付渠道配置：分页查询、新增、修改、删除")
@RestController
@RequestMapping("/v1/admin/payChannelConfig")
public class PayChannelConfigController {

    private final PayChannelConfigService payChannelConfigservice;

    public PayChannelConfigController(PayChannelConfigService payChannelConfigservice) {
        this.payChannelConfigservice = payChannelConfigservice;
    }

    @Operation(summary = "通过id查询支付渠道配置")
    @GetMapping("/{id}")
    public PayChannelConfigEntity findById(@PathVariable Long id) {
        return payChannelConfigservice.findById(id);
    }

    @Operation(summary = "分页查询支付渠道配置")
    @PostMapping("/page")
    public ResponsePageEntity<PayChannelConfigEntity> searchByPage(@RequestBody PayChannelConfigConditionEntity condition) {
        return payChannelConfigservice.searchByPage(condition);
    }

    @Operation(summary = "新增支付渠道配置")
    @PostMapping("/insert")
    public RowsDTO insert(@RequestBody PayChannelConfigEntity entity) {
        return new RowsDTO(payChannelConfigservice.insert(entity));
    }

    @Operation(summary = "修改支付渠道配置")
    @PostMapping("/update")
    public RowsDTO update(@RequestBody PayChannelConfigEntity entity) {
        return new RowsDTO(payChannelConfigservice.update(entity));
    }

    @Operation(summary = "批量删除支付渠道配置")
    @PostMapping("/deleteByIds")
    public RowsDTO deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return new RowsDTO(payChannelConfigservice.deleteByIds(ids));
    }
}
