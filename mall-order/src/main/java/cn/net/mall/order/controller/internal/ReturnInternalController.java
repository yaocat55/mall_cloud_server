package cn.net.mall.order.controller.internal;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.order.dto.IdDTO;
import cn.net.mall.order.dto.OrderReturnApplyDTO;
import cn.net.mall.order.dto.OrderReturnConditionDTO;
import cn.net.mall.order.dto.RowsDTO;
import cn.net.mall.order.entity.OrderReturnApplyConditionEntity;
import cn.net.mall.order.entity.OrderReturnApplyEntity;
import cn.net.mall.order.mapper.OrderReturnApplyMapper;
import cn.net.mall.order.service.OrderReturnApplyService;
import cn.net.mall.util.FillUserUtil;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 内部服务-退货管理.
 *
 * <p>供 admin-bff 通过 Feign 调用，无 @PreAuthorize 校验，信任内部调用方。</p>
 */
@Tag(name = "内部服务-退货管理", description = "内部微服务：供 admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/return")
@RequiredArgsConstructor
public class ReturnInternalController {

    private final OrderReturnApplyService orderReturnApplyService;
    private final OrderReturnApplyMapper orderReturnApplyMapper;

    @Operation(summary = "分页查询退货列表（内部）", description = "无条件限制，查询全部退货申请")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<?> searchByPage(@RequestBody OrderReturnConditionDTO condition) {
        OrderReturnApplyConditionEntity entity = BeanUtil.toBean(condition, OrderReturnApplyConditionEntity.class);
        return orderReturnApplyService.searchByPage(entity);
    }

    @Operation(summary = "查询退货详情（内部）")
    @GetMapping("/findDetailById")
    public Object findDetailById(@RequestParam("id") Long id) {
        return orderReturnApplyService.findById(id);
    }

    @Operation(summary = "审批退货（内部）", description = "同意退货退款申请")
    @PostMapping("/approve")
    public RowsDTO approve(@RequestBody OrderReturnApplyDTO dto) {
        OrderReturnApplyEntity entity = BeanUtil.toBean(dto, OrderReturnApplyEntity.class);
        return new RowsDTO(orderReturnApplyService.approve(entity));
    }

    @Operation(summary = "拒绝退货（内部）", description = "驳回退货退款申请")
    @PostMapping("/reject")
    public RowsDTO reject(@RequestBody OrderReturnApplyDTO dto) {
        OrderReturnApplyEntity entity = BeanUtil.toBean(dto, OrderReturnApplyEntity.class);
        return new RowsDTO(orderReturnApplyService.reject(entity));
    }

    @Operation(summary = "新增退货申请（内部）")
    @PostMapping("/insert")
    public IdDTO insert(@RequestBody OrderReturnApplyDTO dto) {
        OrderReturnApplyEntity entity = BeanUtil.toBean(dto, OrderReturnApplyEntity.class);
        entity.setId(null);
        entity.setApplyTime(new Date());
        entity.setIsDel(0);
        FillUserUtil.fillCreateUserInfo(entity);
        orderReturnApplyMapper.insert(entity);
        return new IdDTO(entity.getId());
    }
}
