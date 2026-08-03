package cn.net.mall.pay.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.pay.dto.RowsDTO;
import cn.net.mall.pay.entity.PayBizConfigEntity;
import cn.net.mall.pay.entity.PayBizConfigConditionEntity;
import cn.net.mall.pay.service.PayBizConfigService;
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
 * 业务渠道接入 接口层（管理端）
 */
@Tag(name = "业务渠道接入", description = "业务渠道接入：分页查询、新增、修改、删除")
@RestController
@RequestMapping("/v1/admin/payBizConfig")
public class PayBizConfigController {

    private final PayBizConfigService payBizConfigservice;

    public PayBizConfigController(PayBizConfigService payBizConfigservice) {
        this.payBizConfigservice = payBizConfigservice;
    }

    @Operation(summary = "通过id查询业务渠道接入")
    @GetMapping("/{id}")
    public ApiResult<PayBizConfigEntity> findById(@PathVariable Long id) {
        return ApiResultUtil.success(payBizConfigservice.findById(id));
    }

    @Operation(summary = "分页查询业务渠道接入")
    @PostMapping("/page")
    public ApiResult<ResponsePageEntity<PayBizConfigEntity>> searchByPage(@RequestBody PayBizConfigConditionEntity condition) {
        return ApiResultUtil.success(payBizConfigservice.searchByPage(condition));
    }

    @Operation(summary = "新增业务渠道接入")
    @PostMapping("/insert")
    public ApiResult<RowsDTO> insert(@RequestBody PayBizConfigEntity entity) {
        return ApiResultUtil.success(new RowsDTO(payBizConfigservice.insert(entity)));
    }

    @Operation(summary = "修改业务渠道接入")
    @PostMapping("/update")
    public ApiResult<RowsDTO> update(@RequestBody PayBizConfigEntity entity) {
        return ApiResultUtil.success(new RowsDTO(payBizConfigservice.update(entity)));
    }

    @Operation(summary = "批量删除业务渠道接入")
    @PostMapping("/deleteByIds")
    public ApiResult<RowsDTO> deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return ApiResultUtil.success(new RowsDTO(payBizConfigservice.deleteByIds(ids)));
    }
}
