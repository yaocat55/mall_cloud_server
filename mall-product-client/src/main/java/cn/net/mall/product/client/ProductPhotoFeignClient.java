package cn.net.mall.product.client;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.dto.ProductPhotoDTO;
import cn.net.mall.product.dto.ProductPhotoConditionDTO;
import cn.net.mall.product.client.fallback.ProductFeignFallbackFactory;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import static cn.net.mall.product.constant.AppConstant.PRODUCT_SERVICE_NAME;

@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "productPhotoFeignClient")
public interface ProductPhotoFeignClient {

    @Operation(summary = "分页查询商品图片")
    @PostMapping("/v1/productPhoto/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody ProductPhotoConditionDTO condition);

    @Operation(summary = "查询商品图片详情")
    @GetMapping("/v1/productPhoto/findById")
    Object findById(@RequestParam("id") Long id);

    @Operation(summary = "新增商品图片")
    @PostMapping("/v1/internal/productPhoto/insert")
    int insert(@RequestBody ProductPhotoDTO entity);

    @Operation(summary = "修改商品图片")
    @PostMapping("/v1/internal/productPhoto/update")
    int update(@RequestBody ProductPhotoDTO entity);

    @Operation(summary = "删除商品图片")
    @PostMapping("/v1/internal/productPhoto/deleteByIds")
    int deleteByIds(@RequestBody List<Long> ids);
}
