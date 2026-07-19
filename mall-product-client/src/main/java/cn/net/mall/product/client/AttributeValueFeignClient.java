package cn.net.mall.product.client;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.dto.AttributeValueDTO;
import cn.net.mall.product.dto.AttributeValueConditionDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

import static cn.net.mall.product.constant.AppConstant.PRODUCT_SERVICE_NAME;

/**
 * 属性值 Feign 客户端
 *
 * @date 2025/07/03
 */
@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "attributeValueFeignClient")
public interface AttributeValueFeignClient {

    @Operation(summary = "分页查询属性值", description = "按条件分页查询属性值列表，请求参数包含 pageNo、pageSize 及可选筛选条件（如属性ID、属性值名称等）")
    @PostMapping("/v1/attributeValue/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody AttributeValueConditionDTO condition);

    @Operation(summary = "新增属性值", description = "新增属性值记录，请求体包含属性ID、属性值名称、排序等字段")
    @PostMapping("/v1/internal/attributeValue/insert")
    int insert(@RequestBody AttributeValueDTO entity);

    @Operation(summary = "修改属性值", description = "修改属性值记录，根据 ID 更新属性值名称、排序等字段")
    @PostMapping("/v1/internal/attributeValue/update")
    int update(@RequestBody AttributeValueDTO entity);

    @Operation(summary = "删除属性值", description = "根据 ID 列表批量删除属性值记录")
    @PostMapping("/v1/internal/attributeValue/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}
