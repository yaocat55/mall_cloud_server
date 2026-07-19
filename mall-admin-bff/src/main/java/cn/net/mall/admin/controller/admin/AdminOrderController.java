package cn.net.mall.admin.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.order.client.OrderFeignClient;
import cn.net.mall.order.dto.OrderConditionDTO;
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
@Tag(name = "订单管理", description = "订单查询、退货审核（只读操作）")
public class AdminOrderController {
    private final OrderFeignClient orderFeignClient;

    @Operation(summary = "分页查询订单", description = "多条件分页查询订单列表，支持按订单号、状态、时间等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/page")
    public ApiResult<ResponsePageEntity<?>> searchPage(@RequestBody OrderConditionDTO c) {
        return ApiResultUtil.success(orderFeignClient.searchByPage(c));
    }

    // ========== 退货/退款管理（只读） ==========

    @Operation(summary = "分页查询退货列表", description = "多条件分页查询退货/退款申请列表", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/return/page")
    public ApiResult<ResponsePageEntity<?>> searchReturnPage(@RequestBody OrderReturnConditionDTO c) {
        return ApiResultUtil.success(orderFeignClient.searchReturnByPage(c));
    }

    @Operation(summary = "查询退货详情", description = "根据 ID 查询退货/退款申请的详细信息", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/return/detail")
    public ApiResult<Object> findReturnById(@RequestParam("id") Long id) {
        return ApiResultUtil.success(orderFeignClient.findReturnById(id));
    }
}
