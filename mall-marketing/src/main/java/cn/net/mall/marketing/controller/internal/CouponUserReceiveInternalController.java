package cn.net.mall.marketing.controller.internal;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.marketing.entity.CouponUserReceiveConditionEntity;
import cn.net.mall.marketing.entity.CouponUserReceiveEntity;
import cn.net.mall.marketing.service.CouponUserReceiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "内部服务-领券记录", description = "内部微服务：admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/couponUserReceive")
public class CouponUserReceiveInternalController {
    private final CouponUserReceiveService couponUserReceiveService;

    public CouponUserReceiveInternalController(CouponUserReceiveService couponUserReceiveService) {
        this.couponUserReceiveService = couponUserReceiveService;
    }

    @Operation(summary = "通过id查询领券记录", description = "内部服务：根据ID查询领券记录")
    @GetMapping("/findById")
    public CouponUserReceiveEntity findById(Long id) {
        return couponUserReceiveService.findById(id);
    }

    @Operation(summary = "分页查询领券记录", description = "内部服务：分页查询领券记录")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<CouponUserReceiveEntity> searchByPage(@RequestBody CouponUserReceiveConditionEntity condition) {
        return couponUserReceiveService.searchByPage(condition);
    }

    @Operation(summary = "新增领券记录", description = "内部服务：新增领券记录")
    @PostMapping("/insert")
    public int insert(@RequestBody CouponUserReceiveEntity entity) {
        return couponUserReceiveService.insert(entity);
    }

    @Operation(summary = "修改领券记录", description = "内部服务：修改领券记录")
    @PostMapping("/update")
    public int update(@RequestBody CouponUserReceiveEntity entity) {
        return couponUserReceiveService.update(entity);
    }

    @Operation(summary = "批量删除领券记录", description = "内部服务：批量删除领券记录")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return couponUserReceiveService.deleteByIds(ids);
    }
}
