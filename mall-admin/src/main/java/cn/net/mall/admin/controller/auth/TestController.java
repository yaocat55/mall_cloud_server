package cn.net.mall.admin.controller.auth;

import cn.net.mall.basic.client.DictFeignClient;
import cn.net.mall.basic.dto.DictDetailConditionDTO;
import cn.net.mall.basic.dto.DictDetailDTO;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "测试接口", description = "OpenFeign 连通性测试。无需认证，公开接口")
@RestController
@RequestMapping("/v1/test")
public class TestController {

    private final DictFeignClient dictFeignClient;

    public TestController(DictFeignClient dictFeignClient) {
        this.dictFeignClient = dictFeignClient;
    }

    @Operation(summary = "测试 OpenFeign 调用", description = "无需认证 | 无参，测试 admin 到 basic 服务的 Feign 连通性")
    @GetMapping("/testOpenFeign")
    public List<DictDetailDTO> testOpenFeign() {
        DictDetailConditionDTO dictDetailConditionDTO = new DictDetailConditionDTO();
        List<DictDetailDTO> dictDetailDTOS = dictFeignClient.searchDictDetail(dictDetailConditionDTO);
        if (CollectionUtils.isEmpty(dictDetailDTOS)) {
            return Lists.newArrayList();
        }
        return dictDetailDTOS;
    }
}
