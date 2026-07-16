package cn.net.mall.admin.controller.admin;

import cn.net.mall.admin.dto.IdsDTO;
import cn.net.mall.basic.client.*;
import cn.net.mall.basic.dto.*;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "新增图片", description = "新增图片记录，关联已上传的图片文件", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/photo/insert")
    public ApiResult<Integer> insertPhoto(@RequestBody CommonPhotoDTO entity) {
        return ApiResultUtil.success(photoFeignClient.insert(entity));
    }

    @Operation(summary = "修改图片", description = "修改图片记录信息，如图片名称、所属分组等", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/photo/update")
    public ApiResult<Integer> updatePhoto(@RequestBody CommonPhotoDTO entity) {
        return ApiResultUtil.success(photoFeignClient.update(entity));
    }

    @Operation(summary = "删除图片", description = "根据ID列表批量删除图片记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/photo/delete")
    public ApiResult<Integer> deletePhoto(@RequestBody IdsDTO dto) {
        return ApiResultUtil.success(photoFeignClient.deleteByIds(dto.getIds()));
    }

    // ============================== 图片分组管理 ==============================

    @Operation(summary = "分页查询图片分组", description = "分页查询图片分组列表，支持按分组名称等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/photoGroup/page")
    public ApiResult<ResponsePageEntity> searchPhotoGroupPage(@RequestBody CommonPhotoGroupConditionDTO condition) {
        return ApiResultUtil.success(photoGroupFeignClient.searchByPage(condition));
    }

    @Operation(summary = "新增图片分组", description = "新增图片分组，用于对图片进行分类管理", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/photoGroup/insert")
    public ApiResult<Integer> insertPhotoGroup(@RequestBody CommonPhotoGroupDTO entity) {
        return ApiResultUtil.success(photoGroupFeignClient.insert(entity));
    }

    @Operation(summary = "修改图片分组", description = "修改图片分组信息，如分组名称、排序等", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/photoGroup/update")
    public ApiResult<Integer> updatePhotoGroup(@RequestBody CommonPhotoGroupDTO entity) {
        return ApiResultUtil.success(photoGroupFeignClient.update(entity));
    }

    @Operation(summary = "删除图片分组", description = "根据ID列表批量删除图片分组", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/photoGroup/delete")
    public ApiResult<Integer> deletePhotoGroup(@RequestBody IdsDTO dto) {
        return ApiResultUtil.success(photoGroupFeignClient.deleteByIds(dto.getIds()));
    }

    // ============================== 敏感词管理 ==============================

    @Operation(summary = "分页查询敏感词", description = "分页查询敏感词列表，支持按敏感词内容等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/sensitiveWord/page")
    public ApiResult<ResponsePageEntity> searchSensitiveWordPage(@RequestBody CommonSensitiveWordConditionDTO condition) {
        return ApiResultUtil.success(sensitiveWordFeignClient.searchByPage(condition));
    }

    @Operation(summary = "新增敏感词", description = "新增敏感词，添加到敏感词过滤库", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/sensitiveWord/insert")
    public ApiResult<Integer> insertSensitiveWord(@RequestBody CommonSensitiveWordDTO entity) {
        return ApiResultUtil.success(sensitiveWordFeignClient.insert(entity));
    }

    @Operation(summary = "修改敏感词", description = "修改敏感词内容或状态", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/sensitiveWord/update")
    public ApiResult<Integer> updateSensitiveWord(@RequestBody CommonSensitiveWordDTO entity) {
        return ApiResultUtil.success(sensitiveWordFeignClient.update(entity));
    }

    @Operation(summary = "删除敏感词", description = "根据ID列表批量删除敏感词", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/sensitiveWord/delete")
    public ApiResult<Integer> deleteSensitiveWord(@RequestBody IdsDTO dto) {
        return ApiResultUtil.success(sensitiveWordFeignClient.deleteByIds(dto.getIds()));
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

    @Operation(summary = "全员推送通知", description = "向所有用户推送消息通知", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/notify/push/all")
    public ApiResult<Void> pushNotifyToAll(@RequestBody CommonNotifyDTO entity) {
        notifyFeignClient.pushToAll(entity);
        return ApiResultUtil.success();
    }

    @Operation(summary = "指定用户推送通知", description = "向指定用户推送消息通知", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/notify/push/user")
    public ApiResult<Void> pushNotifyToUser(@RequestBody CommonNotifyDTO entity) {
        notifyFeignClient.pushToUser(entity);
        return ApiResultUtil.success();
    }

    // ============================== 消息通知管理 CRUD ==============================

    @Operation(summary = "新增通知", description = "新增一条消息通知记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/notify/insert")
    public ApiResult<Integer> insertNotify(@RequestBody CommonNotifyDTO entity) {
        return ApiResultUtil.success(notifyFeignClient.insert(entity));
    }

    @Operation(summary = "修改通知", description = "修改指定消息通知记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/notify/update")
    public ApiResult<Integer> updateNotify(@RequestBody CommonNotifyDTO entity) {
        return ApiResultUtil.success(notifyFeignClient.update(entity));
    }

    @Operation(summary = "删除通知", description = "根据ID列表批量删除消息通知记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/notify/delete")
    public ApiResult<Integer> deleteNotify(@RequestBody IdsDTO dto) {
        return ApiResultUtil.success(notifyFeignClient.deleteByIds(dto.getIds()));
    }
}
