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

@FeignClient(value = BASIC_SERVICE_NAME, contextId = "dictFeignClient")
public interface DictFeignClient {

    /**
     * 根据条件查询数据字典详情列表
     *
     * @param dictDetailConditionDTO 条件
     * @return 数据字典详情列表
     */
    @Operation(summary = "根据条件查询数据字典详情列表", description = "根据条件查询数据字典详情列表")
    @PostMapping("/v1/dictDetail/searchDictDetail")
    List<DictDetailDTO> searchDictDetail(@RequestBody @NotNull DictDetailConditionDTO dictDetailConditionDTO);

}
