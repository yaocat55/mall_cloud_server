package cn.net.mall.order.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.order.dto.RowsDTO;
import cn.net.mall.order.entity.OrderReturnApplyConditionEntity;
import cn.net.mall.order.entity.OrderReturnApplyEntity;
import cn.net.mall.order.service.OrderReturnApplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 退货/退款审核管理（管理端）.
 *
 * <p>供 admin-bff 调用，审批用户提交的退货退款申请。</p>
 */
@Tag(name = "退货退款审核", description = "管理后台：退货退款申请审批。需 Bearer Token + admin 角色")
@Validated
@RestController
@RequestMapping("/v1/trade/return")
@RequiredArgsConstructor
public class OrderReturnApprovalController {

    private final OrderReturnApplyService orderReturnApplyService;

    @Operation(summary = "分页查询退货列表（管理端）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<OrderReturnApplyEntity> searchByPage(@RequestBody OrderReturnApplyConditionEntity condition) {
        return orderReturnApplyService.searchByPage(condition);
    }

    @Operation(summary = "查询退货详情（管理端）")
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/findById")
    public OrderReturnApplyEntity findById(@RequestParam("id") Long id) {
        return orderReturnApplyService.findById(id);
    }

    @Operation(summary = "审批退货（管理端）", description = "同意退货退款申请，状态更新为审核通过")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/approve")
    public RowsDTO approve(@RequestBody OrderReturnApplyEntity entity) {
        return new RowsDTO(orderReturnApplyService.approve(entity));
    }

    @Operation(summary = "拒绝退货（管理端）", description = "驳回退货退款申请，需填写拒绝原因")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/reject")
    public RowsDTO reject(@RequestBody OrderReturnApplyEntity entity) {
        return new RowsDTO(orderReturnApplyService.reject(entity));
    }
}
