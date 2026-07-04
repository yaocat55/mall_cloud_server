package cn.net.mall.admin.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.order.client.OrderFeignClient;
import cn.net.mall.order.dto.OrderDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import cn.net.mall.order.dto.OrderConditionDTO;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/v1/order")
@RequiredArgsConstructor
@Tag(name = "管理后台-订单管理", description = "订单查询与管理接口，需携带 Bearer Token")
public class AdminOrderController {
    private final OrderFeignClient orderFeignClient;

    @Operation(summary = "分页查询订单", description = "多条件分页查询订单列表，支持按订单号、状态、时间等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/page")
    public ResponsePageEntity<?> searchPage(@RequestBody OrderConditionDTO c) { return orderFeignClient.search(c); }
    @Operation(summary = "修改订单", description = "修改已有订单的信息，包括收货地址、备注等", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/update")
    public int update(@RequestBody OrderDTO dto) { return orderFeignClient.update(dto); }
    @Operation(summary = "删除订单", description = "根据 ID 列表批量删除订单", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/delete")
    public int delete(@RequestBody @NotNull List<Long> ids) { return orderFeignClient.delete(ids); }

    // ========== 交易配送地址 (tradeDeliveryAddress) ==========

    @Operation(summary = "分页查询交易配送地址", description = "多条件分页查询订单的配送地址列表", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/deliveryAddress/page")
    public ResponsePageEntity<?> searchDeliveryAddressPage(@RequestBody Map<String, Object> c) { return orderFeignClient.searchDeliveryAddressPage(c); }

    @Operation(summary = "新增交易配送地址", description = "新增一条订单配送地址记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/deliveryAddress/insert")
    public int insertDeliveryAddress(@RequestBody Object e) { return orderFeignClient.insertDeliveryAddress(e); }

    @Operation(summary = "修改交易配送地址", description = "修改已有订单配送地址的信息", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/deliveryAddress/update")
    public int updateDeliveryAddress(@RequestBody Object e) { return orderFeignClient.updateDeliveryAddress(e); }

    @Operation(summary = "删除交易配送地址", description = "根据 ID 列表批量删除订单配送地址", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/deliveryAddress/delete")
    public int deleteDeliveryAddress(@RequestBody @NotNull List<Long> ids) { return orderFeignClient.deleteDeliveryAddressByIds(ids); }

    @Operation(summary = "查询交易配送地址详情", description = "根据 ID 查询单个配送地址的完整信息", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/deliveryAddress/detail")
    public Object findDeliveryAddressById(@RequestParam("id") Long id) { return orderFeignClient.findDeliveryAddressById(id); }

    // ========== 退货/退款管理 ==========

    @Operation(summary = "分页查询退货列表", description = "多条件分页查询退货/退款申请列表", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/return/page")
    public ResponsePageEntity<?> searchReturnPage(@RequestBody Map<String, Object> c) { return orderFeignClient.searchReturnByPage(c); }

    @Operation(summary = "审批退货", description = "审批通过退货/退款申请", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/return/approve")
    public int approveReturn(@RequestBody Object e) { return orderFeignClient.approveReturn(e); }

    @Operation(summary = "拒绝退货", description = "驳回退货/退款申请", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/return/reject")
    public int rejectReturn(@RequestBody Object e) { return orderFeignClient.rejectReturn(e); }

    @Operation(summary = "查询退货详情", description = "根据 ID 查询退货/退款申请的详细信息", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/return/detail")
    public Object findReturnById(@RequestParam("id") Long id) { return orderFeignClient.findReturnById(id); }
}