package cn.net.mall.admin.controller.admin;

import cn.net.mall.admin.dto.IdsDTO;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.order.client.OrderFeignClient;
import cn.net.mall.order.dto.OrderConditionDTO;
import cn.net.mall.order.dto.OrderDTO;
import cn.net.mall.order.dto.OrderReturnApplyDTO;
import cn.net.mall.order.dto.OrderReturnConditionDTO;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/v1/order")
@RequiredArgsConstructor
@Tag(name = "订单管理", description = "订单查询、退货审核")
public class AdminOrderController {
    private final OrderFeignClient orderFeignClient;

    @Operation(summary = "分页查询订单", description = "多条件分页查询订单列表，支持按订单号、状态、时间等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/page")
    public ApiResult<ResponsePageEntity<?>> searchPage(@RequestBody OrderConditionDTO c) { return ApiResultUtil.success(orderFeignClient.search(c)); }
    @Operation(summary = "修改订单", description = "修改已有订单的信息，包括收货地址、备注等", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/update")
    public ApiResult<Integer> update(@RequestBody OrderDTO dto) {
        return ApiResultUtil.success(orderFeignClient.update(dto).getRows());
    }
    @Operation(summary = "删除订单", description = "根据 ID 列表批量删除订单", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/delete")
    public ApiResult<Integer> delete(@RequestBody IdsDTO dto) {
        return ApiResultUtil.success(orderFeignClient.delete(dto.getIds()).getRows());
    }

    // ========== 退货/退款管理 ==========

    @Operation(summary = "分页查询退货列表", description = "多条件分页查询退货/退款申请列表", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/return/page")
    public ApiResult<ResponsePageEntity<?>> searchReturnPage(@RequestBody OrderReturnConditionDTO c) { return ApiResultUtil.success(orderFeignClient.searchReturnByPage(c)); }

    @Operation(summary = "审批退货", description = "审批通过退货/退款申请，需传入退货单 ID", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/return/approve")
    public ApiResult<Integer> approveReturn(@RequestBody OrderReturnApplyDTO dto) {
        return ApiResultUtil.success(orderFeignClient.approveReturn(dto).getRows());
    }

    @Operation(summary = "拒绝退货", description = "驳回退货/退款申请，需传入退货单 ID 和拒绝原因", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/return/reject")
    public ApiResult<Integer> rejectReturn(@RequestBody OrderReturnApplyDTO dto) {
        return ApiResultUtil.success(orderFeignClient.rejectReturn(dto).getRows());
    }

    @Operation(summary = "查询退货详情", description = "根据 ID 查询退货/退款申请的详细信息", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/return/detail")
    public ApiResult<Object> findReturnById(@RequestParam("id") Long id) { return ApiResultUtil.success(orderFeignClient.findReturnById(id)); }
}
