package cn.net.mall.product.client;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.dto.ProductViewRecordConditionDTO;
import cn.net.mall.product.dto.ProductViewRecordDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static cn.net.mall.product.constant.AppConstant.PRODUCT_SERVICE_NAME;

@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "productViewRecordFeignClient")
public interface ProductViewRecordFeignClient {

    @Operation(summary = "根据条件查询商品浏览记录列表", description = "根据条件查询商品浏览记录列表")
    @PostMapping("/v1/internal/productViewRecord/searchByPage")
    ResponsePageEntity<ProductViewRecordDTO> searchByPage(@RequestBody ProductViewRecordConditionDTO condition);

    @Operation(summary = "新增浏览记录（管理端）", description = "新增一条商品浏览记录")
    @PostMapping("/v1/productViewRecord/insert")
    int insert(@RequestBody Object entity);

    @Operation(summary = "修改浏览记录（管理端）", description = "修改一条已有的商品浏览记录")
    @PostMapping("/v1/productViewRecord/update")
    int update(@RequestBody Object entity);

    @Operation(summary = "删除浏览记录（管理端）", description = "根据ID列表批量删除商品浏览记录")
    @PostMapping("/v1/productViewRecord/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);

    @Operation(summary = "仅返回商品浏览记录数据列表（无总数）", description = "仅返回商品浏览记录数据列表（无总数）")
    @PostMapping("/v1/internal/productViewRecord/searchList")
    java.util.List<ProductViewRecordDTO> searchList(@RequestBody ProductViewRecordConditionDTO condition);
}
