package cn.net.mall.product.client;

import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import static cn.net.mall.product.constant.AppConstant.PRODUCT_SERVICE_NAME;

@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "unitFeignClient")
public interface UnitFeignClient {
    @Operation(summary = "分页查询单位")
    @PostMapping("/v1/unit/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody Map<String, Object> condition);
    @Operation(summary = "新增单位")
    @PostMapping("/v1/unit/insert")
    int insert(@RequestBody Object entity);
    @Operation(summary = "修改单位")
    @PostMapping("/v1/unit/update")
    int update(@RequestBody Object entity);
    @Operation(summary = "删除单位")
    @PostMapping("/v1/unit/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}