package cn.net.mall.admin.controller.admin;

import cn.net.mall.basic.client.*;
import cn.net.mall.basic.dto.*;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 基础数据 BFF 控制器（只保留读聚合）.
 *
 * <p>写操作已迁移至微服务层，前端通过 Gateway 直调 basic/message 服务。</p>
 */
@Slf4j
@RestController
@RequestMapping("/admin/v1/basic")
@RequiredArgsConstructor
@Tag(name = "基础数据", description = "图片库、敏感词、通知、文件上传")
public class AdminBasicController {

    private final PhotoFeignClient photoFeignClient;
    private final PhotoGroupFeignClient photoGroupFeignClient;
    private final SensitiveWordFeignClient sensitiveWordFeignClient;
    private final NotifyFeignClient notifyFeignClient;

    // ============================== 图片管理 ==============================

    @Operation(summary = "分页查询图片", description = "分页查询图片列表，支持按图片名称、分组、上传时间等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/photo/page")
    public ApiResult<ResponsePageEntity> searchPhotoPage(@RequestBody CommonPhotoConditionDTO condition) {
        return ApiResultUtil.success(photoFeignClient.searchByPage(condition));
    }

    // ============================== 图片分组管理 ==============================

    @Operation(summary = "分页查询图片分组", description = "分页查询图片分组列表，支持按分组名称等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/photoGroup/page")
    public ApiResult<ResponsePageEntity> searchPhotoGroupPage(@RequestBody CommonPhotoGroupConditionDTO condition) {
        return ApiResultUtil.success(photoGroupFeignClient.searchByPage(condition));
    }

    // ============================== 敏感词管理 ==============================

    @Operation(summary = "分页查询敏感词", description = "分页查询敏感词列表，支持按敏感词内容等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/sensitiveWord/page")
    public ApiResult<ResponsePageEntity> searchSensitiveWordPage(@RequestBody CommonSensitiveWordConditionDTO condition) {
        return ApiResultUtil.success(sensitiveWordFeignClient.searchByPage(condition));
    }

    @Operation(summary = "校验敏感词", description = "校验文本内容中是否包含敏感词，返回校验结果", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/sensitiveWord/check")
    public ApiResult<Void> checkSensitiveWord(@RequestBody CommonSensitiveWordDTO entity) {
        sensitiveWordFeignClient.checkSensitiveWord(entity);
        return ApiResultUtil.success();
    }

    // ============================== 消息通知管理 ==============================

    @Operation(summary = "分页查询通知列表", description = "分页查询通知消息列表，支持按通知标题、推送状态、推送时间等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/notify/page")
    public ApiResult<ResponsePageEntity> searchNotifyPage(@RequestBody CommonNotifyConditionDTO condition) {
        return ApiResultUtil.success(notifyFeignClient.searchByPage(condition));
    }
}
