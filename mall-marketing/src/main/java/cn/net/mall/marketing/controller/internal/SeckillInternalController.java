package cn.net.mall.marketing.controller.internal;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.marketing.entity.SeckillProductConditionEntity;
import cn.net.mall.marketing.entity.SeckillProductEntity;
import cn.net.mall.marketing.service.SeckillProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "内部服务-秒杀", description = "内部微服务：admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/seckill")
public class SeckillInternalController {
    private final SeckillProductService seckillProductService;

    public SeckillInternalController(SeckillProductService seckillProductService) {
        this.seckillProductService = seckillProductService;
    }

    @Operation(summary = "通过id查询秒杀商品信息", description = "内部服务：根据ID查询秒杀商品信息")
    @GetMapping("/findById")
    public SeckillProductEntity findById(Long id) {
        return seckillProductService.findById(id);
    }

    @Operation(summary = "根据条件查询秒杀商品列表", description = "内部服务：分页查询秒杀商品列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<SeckillProductEntity> searchByPage(@RequestBody SeckillProductConditionEntity condition) {
        return seckillProductService.searchByPage(condition);
    }

    @Operation(summary = "新增秒杀商品", description = "内部服务：新增秒杀商品")
    @PostMapping("/insert")
    public void insert(@RequestBody SeckillProductEntity entity) {
        seckillProductService.insert(entity);
    }

    @Operation(summary = "修改秒杀商品", description = "内部服务：修改秒杀商品")
    @PostMapping("/update")
    public void update(@RequestBody SeckillProductEntity entity) {
        seckillProductService.update(entity);
    }

    @Operation(summary = "批量删除秒杀商品", description = "内部服务：批量删除秒杀商品")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return seckillProductService.deleteByIds(ids);
    }
}
