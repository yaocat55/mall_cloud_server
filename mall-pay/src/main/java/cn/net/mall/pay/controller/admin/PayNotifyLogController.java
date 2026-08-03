package cn.net.mall.pay.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.pay.dto.RowsDTO;
import cn.net.mall.pay.entity.PayNotifyLogEntity;
import cn.net.mall.pay.entity.PayNotifyLogConditionEntity;
import cn.net.mall.pay.service.PayNotifyLogService;
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
 * 支付回调日志 接口层（管理端）
 */
@Tag(name = "支付回调日志", description = "支付回调日志：分页查询、新增、修改、删除")
@RestController
@RequestMapping("/v1/admin/payNotifyLog")
public class PayNotifyLogController {

    private final PayNotifyLogService payNotifyLogservice;

    public PayNotifyLogController(PayNotifyLogService payNotifyLogservice) {
        this.payNotifyLogservice = payNotifyLogservice;
    }

    @Operation(summary = "通过id查询支付回调日志")
    @GetMapping("/{id}")
    public ApiResult<PayNotifyLogEntity> findById(@PathVariable Long id) {
        return ApiResultUtil.success(payNotifyLogservice.findById(id));
    }

    @Operation(summary = "分页查询支付回调日志")
    @PostMapping("/page")
    public ApiResult<ResponsePageEntity<PayNotifyLogEntity>> searchByPage(@RequestBody PayNotifyLogConditionEntity condition) {
        return ApiResultUtil.success(payNotifyLogservice.searchByPage(condition));
    }

    @Operation(summary = "新增支付回调日志")
    @PostMapping("/insert")
    public ApiResult<RowsDTO> insert(@RequestBody PayNotifyLogEntity entity) {
        return ApiResultUtil.success(new RowsDTO(payNotifyLogservice.insert(entity)));
    }

    @Operation(summary = "修改支付回调日志")
    @PostMapping("/update")
    public ApiResult<RowsDTO> update(@RequestBody PayNotifyLogEntity entity) {
        return ApiResultUtil.success(new RowsDTO(payNotifyLogservice.update(entity)));
    }

    @Operation(summary = "批量删除支付回调日志")
    @PostMapping("/deleteByIds")
    public ApiResult<RowsDTO> deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return ApiResultUtil.success(new RowsDTO(payNotifyLogservice.deleteByIds(ids)));
    }
}
