package cn.net.mall.pay.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.pay.dto.RowsDTO;
import cn.net.mall.pay.entity.PayOrderEntity;
import cn.net.mall.pay.entity.PayOrderConditionEntity;
import cn.net.mall.pay.service.PayOrderService;
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
 * 支付订单 接口层（管理端）
 */
@Tag(name = "支付订单", description = "支付订单：分页查询、新增、修改、删除")
@RestController
@RequestMapping("/v1/admin/payOrder")
public class PayOrderController {

    private final PayOrderService payOrderservice;

    public PayOrderController(PayOrderService payOrderservice) {
        this.payOrderservice = payOrderservice;
    }

    @Operation(summary = "通过id查询支付订单")
    @GetMapping("/{id}")
    public PayOrderEntity findById(@PathVariable Long id) {
        return payOrderservice.findById(id);
    }

    @Operation(summary = "分页查询支付订单")
    @PostMapping("/page")
    public ResponsePageEntity<PayOrderEntity> searchByPage(@RequestBody PayOrderConditionEntity condition) {
        return payOrderservice.searchByPage(condition);
    }

    @Operation(summary = "新增支付订单")
    @PostMapping("/insert")
    public RowsDTO insert(@RequestBody PayOrderEntity entity) {
        return new RowsDTO(payOrderservice.insert(entity));
    }

    @Operation(summary = "修改支付订单")
    @PostMapping("/update")
    public RowsDTO update(@RequestBody PayOrderEntity entity) {
        return new RowsDTO(payOrderservice.update(entity));
    }

    @Operation(summary = "批量删除支付订单")
    @PostMapping("/deleteByIds")
    public RowsDTO deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return new RowsDTO(payOrderservice.deleteByIds(ids));
    }
}
