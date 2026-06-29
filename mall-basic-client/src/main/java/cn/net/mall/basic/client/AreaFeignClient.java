package cn.net.mall.basic.client;

import cn.net.mall.basic.dto.AreaDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static cn.net.mall.basic.constant.AppConstant.BASIC_SERVICE_NAME;

/**
 * [Service] Feign 客户端
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>mall-auth（权限服务）— 查询地区数据</li>
 *   <li>mall-product（商品服务）— 查询地区数据</li>
 *   <li>mall-order（订单服务）— 查询地区数据</li>
 * </ul>
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
    @Operation(summary = "根据parentId查询地区列表", description = "内部Feign调用：根据父级ID查询子级地区列表")
    @GetMapping("/v1/commonArea/queryByParentId")
    List<AreaDTO> queryByParentId(@RequestParam("parentId") Long parentId);
}
