package cn.net.mall.marketing.controller.internal;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.marketing.entity.CouponUserProvideConditionEntity;
import cn.net.mall.marketing.entity.CouponUserProvideEntity;
import cn.net.mall.marketing.service.CouponUserProvideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "内部服务-发券记录", description = "内部微服务：admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/couponUserProvide")
public class CouponUserProvideInternalController {
    private final CouponUserProvideService couponUserProvideService;

    public CouponUserProvideInternalController(CouponUserProvideService couponUserProvideService) {
        this.couponUserProvideService = couponUserProvideService;
    }

    @Operation(summary = "通过id查询发券记录", description = "内部服务：根据ID查询发券记录")
    @GetMapping("/findById")
    public CouponUserProvideEntity findById(Long id) {
        return couponUserProvideService.findById(id);
    }

    @Operation(summary = "分页查询发券记录", description = "内部服务：分页查询发券记录")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<CouponUserProvideEntity> searchByPage(@RequestBody CouponUserProvideConditionEntity condition) {
        return couponUserProvideService.searchByPage(condition);
    }

    @Operation(summary = "新增发券记录", description = "内部服务：新增发券记录")
    @PostMapping("/insert")
    public int insert(@RequestBody CouponUserProvideEntity entity) {
        return couponUserProvideService.insert(entity);
    }

    @Operation(summary = "修改发券记录", description = "内部服务：修改发券记录")
    @PostMapping("/update")
    public int update(@RequestBody CouponUserProvideEntity entity) {
        return couponUserProvideService.update(entity);
    }

    @Operation(summary = "批量删除发券记录", description = "内部服务：批量删除发券记录")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return couponUserProvideService.deleteByIds(ids);
    }
}
