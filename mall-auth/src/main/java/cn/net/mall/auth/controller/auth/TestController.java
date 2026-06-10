package cn.net.mall.auth.controller.auth;

import cn.net.mall.annotation.NoLogin;
import cn.net.mall.basic.client.DictFeignClient;
import cn.net.mall.basic.dto.DictDetailConditionDTO;
import cn.net.mall.basic.dto.DictDetailDTO;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/test")
public class TestController {

    private final DictFeignClient dictFeignClient;

    public TestController(DictFeignClient dictFeignClient) {
        this.dictFeignClient = dictFeignClient;
    }

    @NoLogin
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
