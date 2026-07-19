package cn.net.mall.product.client;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.dto.ProductGroupDTO;
import cn.net.mall.product.dto.ProductGroupConditionDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

import static cn.net.mall.product.constant.AppConstant.PRODUCT_SERVICE_NAME;

/**
 * 商品组 Feign 客户端
 *
 * @date 2025/07/03
 */
@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "productGroupFeignClient")
public interface ProductGroupFeignClient {

    @Operation(summary = "分页查询商品组", description = "按条件分页查询商品组列表，请求参数包含 pageNo、pageSize 及可选筛选条件（如商品组名称、状态等）")
    @PostMapping("/v1/productGroup/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody ProductGroupConditionDTO condition);

    @Operation(summary = "新增商品组", description = "新增商品组记录，请求体包含商品组名称、排序、状态等字段")
    @PostMapping("/v1/internal/productGroup/insert")
    int insert(@RequestBody ProductGroupDTO entity);

    @Operation(summary = "修改商品组", description = "修改商品组记录，根据 ID 更新商品组名称、排序、状态等字段")
    @PostMapping("/v1/internal/productGroup/update")
    int update(@RequestBody ProductGroupDTO entity);

    @Operation(summary = "删除商品组", description = "根据 ID 列表批量删除商品组记录")
    @PostMapping("/v1/internal/productGroup/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}
