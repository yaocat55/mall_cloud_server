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

@Slf4j
@RestController
@RequestMapping("/admin/v1/order")
@RequiredArgsConstructor
@Tag(name = "管理后台-订单管理", description = "订单查询与管理接口，需携带 Bearer Token")
public class AdminOrderController {
    private final OrderFeignClient orderFeignClient;

    @Operation(summary = "分页查询订单", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/page")
    public ResponsePageEntity<?> searchPage(@RequestBody OrderConditionDTO c) { return orderFeignClient.search(c); }
    @Operation(summary = "修改订单", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/update")
    public int update(@RequestBody OrderDTO dto) { return orderFeignClient.update(dto); }
    @Operation(summary = "删除订单", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/delete")
    public int delete(@RequestBody @NotNull List<Long> ids) { return orderFeignClient.delete(ids); }
}