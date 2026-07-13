package cn.net.mall.order.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.order.dto.RowsDTO;
import cn.net.mall.order.entity.OrderDeliveryAddressConditionEntity;
import cn.net.mall.order.entity.OrderDeliveryAddressEntity;
import cn.net.mall.order.service.OrderDeliveryAddressService;
import cn.net.mall.util.FillUserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 交易配送地址管理（管理端）.
 *
 * <p>供 admin-bff 调用，管理所有用户的订单配送地址。</p>
 */
@Tag(name = "交易配送地址管理", description = "管理后台：订单配送地址 CRUD。需 Bearer Token + admin 角色")
@Validated
@RestController
@RequestMapping("/v1/tradeDeliveryAddress")
@RequiredArgsConstructor
public class OrderDeliveryAddressController {

    private final OrderDeliveryAddressService orderDeliveryAddressService;

    @Operation(summary = "分页查询配送地址（管理端）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<OrderDeliveryAddressEntity> searchByPage(@RequestBody OrderDeliveryAddressConditionEntity condition) {
        return orderDeliveryAddressService.searchByPage(condition);
    }

    @Operation(summary = "新增配送地址（管理端）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/insert")
    public RowsDTO insert(@RequestBody OrderDeliveryAddressEntity entity) {
        FillUserUtil.fillCreateUserInfo(entity);
        return new RowsDTO(orderDeliveryAddressService.insert(entity));
    }

    @Operation(summary = "修改配送地址（管理端）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/update")
    public RowsDTO update(@RequestBody OrderDeliveryAddressEntity entity) {
        FillUserUtil.fillUpdateUserInfo(entity);
        return new RowsDTO(orderDeliveryAddressService.update(entity));
    }

    @Operation(summary = "删除配送地址（管理端）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/deleteByIds")
    public RowsDTO deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return new RowsDTO(orderDeliveryAddressService.deleteByIds(ids));
    }

    @Operation(summary = "查询配送地址详情（管理端）")
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/findById")
    public OrderDeliveryAddressEntity findById(@RequestParam("id") Long id) {
        return orderDeliveryAddressService.findById(id);
    }
}
