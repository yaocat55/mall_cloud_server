package cn.net.mall.basic.client;

import cn.net.mall.basic.dto.CommonSensitiveWordConditionDTO;
import cn.net.mall.basic.dto.CommonSensitiveWordDTO;
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
*   - mall-admin-api（管理后台）— 敏感词过滤配置管理
 * 
*
 * @date 2025/7/3
 */
@FeignClient(value = BASIC_SERVICE_NAME, contextId = "sensitiveWordFeignClient")
public interface SensitiveWordFeignClient {

    @Operation(summary = "通过id查询敏感词信息", description = "内部Feign调用：根据主键查询敏感词")
    @GetMapping("/v1/commonSensitiveWord/findById")
    Object findById(@RequestParam("id") Long id);

    @Operation(summary = "校验敏感词", description = "校验文本内容中是否包含敏感词，返回校验结果")
    @PostMapping("/v1/commonSensitiveWord/checkSensitiveWord")
    void checkSensitiveWord(@RequestBody CommonSensitiveWordDTO entity);

    @Operation(summary = "分页查询敏感词（管理端）", description = "分页查询敏感词列表，支持按敏感词内容等条件筛选")
    @PostMapping("/v1/commonSensitiveWord/searchByPage")
    ResponsePageEntity<CommonSensitiveWordDTO> searchByPage(@RequestBody CommonSensitiveWordConditionDTO condition);

    @Operation(summary = "新增敏感词（管理端）", description = "新增敏感词，添加到敏感词过滤库")
    @PostMapping("/v1/commonSensitiveWord/insert")
    int insert(@RequestBody CommonSensitiveWordDTO entity);

    @Operation(summary = "修改敏感词（管理端）", description = "修改敏感词内容或状态")
    @PostMapping("/v1/commonSensitiveWord/update")
    int update(@RequestBody CommonSensitiveWordDTO entity);

    @Operation(summary = "删除敏感词（管理端）", description = "根据ID列表批量删除敏感词")
    @PostMapping("/v1/commonSensitiveWord/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}
