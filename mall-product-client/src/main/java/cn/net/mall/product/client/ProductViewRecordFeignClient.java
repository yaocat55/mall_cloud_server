package cn.net.mall.product.client;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.dto.ProductViewRecordConditionDTO;
import cn.net.mall.product.dto.ProductViewRecordDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static cn.net.mall.product.constant.AppConstant.PRODUCT_SERVICE_NAME;

@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "productViewRecordFeignClient")
public interface ProductViewRecordFeignClient {

    @Operation(summary = "根据条件查询商品浏览记录列表", description = "根据条件查询商品浏览记录列表")
    @PostMapping("/v1/productViewRecord/searchByPage")
    ResponsePageEntity<ProductViewRecordDTO> searchByPage(@RequestBody ProductViewRecordConditionDTO condition);

    @Operation(summary = "仅返回商品浏览记录数据列表（无总数）", description = "仅返回商品浏览记录数据列表（无总数）")
    @PostMapping("/v1/productViewRecord/searchList")
    java.util.List<ProductViewRecordDTO> searchList(@RequestBody ProductViewRecordConditionDTO condition);
}
