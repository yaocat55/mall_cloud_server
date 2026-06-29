package cn.net.mall.basic.client;

import cn.net.mall.basic.dto.DictDetailConditionDTO;
import cn.net.mall.basic.dto.DictDetailDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static cn.net.mall.basic.constant.AppConstant.BASIC_SERVICE_NAME;

/**
 * [Service] Feign 客户端
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>mall-auth（权限服务）— 查询字典详情</li>
 *   <li>mall-product（商品服务）— 查询字典详情</li>
 *   <li>mall-order（订单服务）— 查询字典详情</li>
 * </ul>
 *
 * @date 2025/5/17 14:42
 */
@FeignClient(value = BASIC_SERVICE_NAME, contextId = "dictFeignClient")
public interface DictFeignClient {

    /**
     * 根据条件查询数据字典详情列表
     *
     * @param dictDetailConditionDTO 条件
     * @return 数据字典详情列表
     */
    @Operation(summary = "根据条件查询数据字典详情列表", description = "内部Feign调用：根据字典编码等条件查询字典明细列表")
    @PostMapping("/v1/dictDetail/searchDictDetail")
    List<DictDetailDTO> searchDictDetail(@RequestBody @NotNull DictDetailConditionDTO dictDetailConditionDTO);

}
