package cn.net.mall.basic.client;

import cn.net.mall.basic.dto.AreaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 地区Feign客户端
 *
 * @date 2024-10-04
 */
@FeignClient(name = "mall-basic-api", contextId = "area")
public interface AreaFeignClient {

    /**
     * 根据parentId获取地区列表
     *
     * @param parentId 上级地区ID
     * @return 地区列表
     */
    @GetMapping("/v1/commonArea/queryByParentId")
    List<AreaDTO> queryByParentId(@RequestParam("parentId") Long parentId);
}
