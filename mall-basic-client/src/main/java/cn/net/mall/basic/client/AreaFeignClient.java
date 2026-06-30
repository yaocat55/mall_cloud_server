package cn.net.mall.basic.client;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.basic.dto.AreaDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

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
    @GetMapping("/v1/internal/commonArea/queryByParentId")
    List<AreaDTO> queryByParentId(@RequestParam("parentId") Long parentId);

    @Operation(summary = "分页查询区域（管理端）")
    @PostMapping("/v1/commonArea/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody Map<String, Object> condition);

    @Operation(summary = "新增区域（管理端）")
    @PostMapping("/v1/commonArea/insert")
    int insert(@RequestBody Object entity);

    @Operation(summary = "修改区域（管理端）")
    @PostMapping("/v1/commonArea/update")
    int update(@RequestBody Object entity);

    @Operation(summary = "删除区域（管理端）")
    @PostMapping("/v1/commonArea/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);

}