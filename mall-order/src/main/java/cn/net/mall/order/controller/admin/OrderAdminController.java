package cn.net.mall.order.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.order.dto.OrderConditionDTO;
import cn.net.mall.order.dto.OrderDTO;
import cn.net.mall.order.dto.OrderReturnConditionDTO;
import cn.net.mall.order.entity.OrderConditionEntity;
import cn.net.mall.order.entity.OrderEntity;
import cn.net.mall.order.entity.OrderReturnApplyConditionEntity;
import cn.net.mall.order.service.OrderReturnApplyService;
import cn.net.mall.order.service.OrderService;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单管理（管理端）.
 *
 * <p>供 admin 前端通过 Gateway 直连，替代原 admin-bff 的订单读接口。</p>
 */
@Tag(name = "订单管理（管理端）", description = "管理后台：订单分页查询、退货审核（只读操作）")
@RestController
@RequestMapping("/v1/admin/order")
@RequiredArgsConstructor
public class OrderAdminController {

    private final OrderService orderService;
    private final OrderReturnApplyService orderReturnApplyService;

    @Operation(summary = "分页查询订单", description = "多条件分页查询订单列表，支持按订单号、状态、时间等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/page")
    public ResponsePageEntity<OrderDTO> searchPage(@RequestBody OrderConditionDTO condition) {
        OrderConditionEntity entity = BeanUtil.copyProperties(condition, OrderConditionEntity.class);
        ResponsePageEntity<OrderEntity> page = orderService.searchByPage(entity);
        List<OrderDTO> dtoList = page.getData().stream()
                .map(e -> BeanUtil.toBean(e, OrderDTO.class))
                .collect(Collectors.toList());
        return ResponsePageEntity.build(condition, page.getTotalCount(), dtoList);
    }

    @Operation(summary = "分页查询退货列表", description = "多条件分页查询退货/退款申请列表", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/return/page")
    public ResponsePageEntity<?> searchReturnPage(@RequestBody OrderReturnConditionDTO condition) {
        OrderReturnApplyConditionEntity entity = BeanUtil.toBean(condition, OrderReturnApplyConditionEntity.class);
        return orderReturnApplyService.searchByPage(entity);
    }

    @Operation(summary = "查询退货详情", description = "根据 ID 查询退货/退款申请的详细信息", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/return/detail")
    public Object findReturnById(@RequestParam("id") Long id) {
        return orderReturnApplyService.findById(id);
    }
}
