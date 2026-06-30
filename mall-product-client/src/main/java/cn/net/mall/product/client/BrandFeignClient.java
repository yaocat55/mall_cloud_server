package cn.net.mall.product.client;

import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import static cn.net.mall.product.constant.AppConstant.PRODUCT_SERVICE_NAME;

@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "brandFeignClient")
public interface BrandFeignClient {
    @Operation(summary = "分页查询品牌")
    @PostMapping("/v1/brand/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody Map<String, Object> condition);
    @Operation(summary = "新增品牌")
    @PostMapping("/v1/brand/insert")
    int insert(@RequestBody Object entity);
    @Operation(summary = "修改品牌")
    @PostMapping("/v1/brand/update")
    int update(@RequestBody Object entity);
    @Operation(summary = "删除品牌")
    @PostMapping("/v1/brand/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}