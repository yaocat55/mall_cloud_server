package cn.net.mall.pay.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.pay.dto.RowsDTO;
import cn.net.mall.pay.entity.PayRefundEntity;
import cn.net.mall.pay.entity.PayRefundConditionEntity;
import cn.net.mall.pay.service.PayRefundService;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
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
 * 支付退款单 接口层（管理端）
 */
@Tag(name = "支付退款单", description = "支付退款单：分页查询、新增、修改、删除")
@RestController
@RequestMapping("/v1/admin/payRefund")
public class PayRefundController {

    private final PayRefundService payRefundservice;

    public PayRefundController(PayRefundService payRefundservice) {
        this.payRefundservice = payRefundservice;
    }

    @Operation(summary = "通过id查询支付退款单")
    @GetMapping("/{id}")
    public ApiResult<PayRefundEntity> findById(@PathVariable Long id) {
        return ApiResultUtil.success(payRefundservice.findById(id));
    }

    @Operation(summary = "分页查询支付退款单")
    @PostMapping("/page")
    public ApiResult<ResponsePageEntity<PayRefundEntity>> searchByPage(@RequestBody PayRefundConditionEntity condition) {
        return ApiResultUtil.success(payRefundservice.searchByPage(condition));
    }

    @Operation(summary = "新增支付退款单")
    @PostMapping("/insert")
    public ApiResult<RowsDTO> insert(@RequestBody PayRefundEntity entity) {
        return ApiResultUtil.success(new RowsDTO(payRefundservice.insert(entity)));
    }

    @Operation(summary = "修改支付退款单")
    @PostMapping("/update")
    public ApiResult<RowsDTO> update(@RequestBody PayRefundEntity entity) {
        return ApiResultUtil.success(new RowsDTO(payRefundservice.update(entity)));
    }

    @Operation(summary = "批量删除支付退款单")
    @PostMapping("/deleteByIds")
    public ApiResult<RowsDTO> deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return ApiResultUtil.success(new RowsDTO(payRefundservice.deleteByIds(ids)));
    }
}
