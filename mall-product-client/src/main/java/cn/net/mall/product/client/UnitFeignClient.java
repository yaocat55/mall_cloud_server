package cn.net.mall.product.client;

import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import cn.net.mall.product.dto.UnitConditionDTO;
import cn.net.mall.product.dto.UnitDTO;
import static cn.net.mall.product.constant.AppConstant.PRODUCT_SERVICE_NAME;

@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "unitFeignClient")
public interface UnitFeignClient {
    @Operation(summary = "分页查询单位")
    @PostMapping("/v1/unit/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody UnitConditionDTO condition);
    @Operation(summary = "新增单位")
    @PostMapping("/v1/internal/unit/insert")
    int insert(@RequestBody UnitDTO entity);
    @Operation(summary = "修改单位")
    @PostMapping("/v1/internal/unit/update")
    int update(@RequestBody UnitDTO entity);
    @Operation(summary = "删除单位")
    @PostMapping("/v1/internal/unit/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}