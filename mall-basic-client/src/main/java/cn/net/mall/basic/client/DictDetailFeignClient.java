package cn.net.mall.basic.client;

import cn.net.mall.basic.dto.DictDetailConditionDTO;
import cn.net.mall.basic.dto.DictDetailDTO;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static cn.net.mall.basic.constant.AppConstant.BASIC_SERVICE_NAME;

/**
 * [Service] Feign 客户端
 * 
* **调用方：**
 * 
*   - mall-admin-api（管理后台）— 字典详情配置管理
 *   - mall-auth（权限服务）— 查询字典详情
 *   - mall-product（商品服务）— 查询字典详情
 * 
*
 * @date 2025/7/3
 */
@FeignClient(value = BASIC_SERVICE_NAME, contextId = "dictDetailFeignClient")
public interface DictDetailFeignClient {

    @Operation(summary = "通过id查询数据字典详情信息", description = "内部Feign调用：根据主键查询字典详情")
    @GetMapping("/v1/dictDetail/findById")
    Object findById(@RequestParam("id") Long id);

    /**
     * 根据条件查询数据字典详情列表
     *
     * @param dictDetailConditionDTO 条件
     * @return 数据字典详情列表
     */
    @Operation(summary = "根据条件查询数据字典详情列表", description = "内部Feign调用：根据字典编码等条件查询字典明细列表")
    @PostMapping("/v1/dictDetail/searchDictDetail")
    List<DictDetailDTO> searchDictDetail(@RequestBody @NotNull DictDetailConditionDTO dictDetailConditionDTO);

    @Operation(summary = "分页查询字典详情（管理端）", description = "分页查询字典详情列表，支持按字典编码、字典标签等条件筛选")
    @PostMapping("/v1/dictDetail/searchByPage")
    ResponsePageEntity<DictDetailDTO> searchByPage(@RequestBody DictDetailConditionDTO condition);

    @Operation(summary = "新增字典详情（管理端）", description = "新增字典详情项，如字典标签、字典值、排序等")
    @PostMapping("/v1/dictDetail/insert")
    int insert(@RequestBody DictDetailDTO entity);

    @Operation(summary = "修改字典详情（管理端）", description = "修改字典详情项，支持更新字典标签、字典值、排序等")
    @PostMapping("/v1/dictDetail/update")
    int update(@RequestBody DictDetailDTO entity);

    @Operation(summary = "删除字典详情（管理端）", description = "根据ID列表批量删除字典详情项")
    @PostMapping("/v1/dictDetail/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}
