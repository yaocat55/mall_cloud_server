package cn.net.mall.admin.controller.admin;

import cn.net.mall.basic.client.*;
import cn.net.mall.basic.dto.*;
import cn.net.mall.entity.ResponsePageEntity;
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
@Tag(name = "管理后台-基础扩展数据", description = "定时任务、图片、敏感词、短信记录等基础数据管理接口，需携带 Bearer Token")
public class AdminBasicController {

    private final JobFeignClient jobFeignClient;
    private final JobLogFeignClient jobLogFeignClient;
    private final PhotoFeignClient photoFeignClient;
    private final PhotoGroupFeignClient photoGroupFeignClient;
    private final SensitiveWordFeignClient sensitiveWordFeignClient;
    private final SmsRecordFeignClient smsRecordFeignClient;
    private final DictDetailFeignClient dictDetailFeignClient;
    private final NotifyFeignClient notifyFeignClient;

    // ============================== 定时任务管理 ==============================

    @Operation(summary = "分页查询定时任务", description = "分页查询定时任务列表，支持按任务名称、任务状态等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/job/page")
    public ResponsePageEntity searchJobPage(@RequestBody CommonJobConditionDTO condition) {
        return jobFeignClient.searchByPage(condition);
    }

    @Operation(summary = "新增定时任务", description = "新增定时任务配置，包括任务名称、Cron表达式、调用目标等参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/job/insert")
    public int insertJob(@RequestBody CommonJobDTO entity) {
        return jobFeignClient.insert(entity);
    }

    @Operation(summary = "修改定时任务", description = "修改定时任务配置，支持更新任务名称、Cron表达式、调用目标等参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/job/update")
    public int updateJob(@RequestBody CommonJobDTO entity) {
        return jobFeignClient.update(entity);
    }

    @Operation(summary = "删除定时任务", description = "根据ID列表批量删除定时任务配置", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/job/delete")
    public int deleteJob(@RequestBody @NotNull List ids) {
        return jobFeignClient.deleteByIds(ids);
    }

    @Operation(summary = "立即执行定时任务", description = "立即触发一次定时任务执行，忽略Cron表达式设定的调度计划", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/job/runNow")
    public void runNowJob(@RequestBody CommonJobDTO entity) {
        jobFeignClient.runNow(entity);
    }

    @Operation(summary = "暂停定时任务", description = "暂停指定定时任务，暂停后任务将不再按照Cron表达式执行", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/job/pause")
    public void pauseJob(@RequestBody CommonJobDTO entity) {
        jobFeignClient.pause(entity);
    }

    @Operation(summary = "恢复定时任务", description = "恢复已暂停的定时任务，任务将重新按照Cron表达式执行", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/job/resume")
    public void resumeJob(@RequestBody CommonJobDTO entity) {
        jobFeignClient.resume(entity);
    }

    // ============================== 定时任务日志管理 ==============================

    @Operation(summary = "分页查询定时任务日志", description = "分页查询定时任务执行日志，支持按任务名称、执行状态、执行时间等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/jobLog/page")
    public ResponsePageEntity searchJobLogPage(@RequestBody CommonJobLogConditionDTO condition) {
        return jobLogFeignClient.searchByPage(condition);
    }

    @Operation(summary = "新增定时任务日志", description = "新增定时任务执行日志记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/jobLog/insert")
    public int insertJobLog(@RequestBody CommonJobLogDTO entity) {
        return jobLogFeignClient.insert(entity);
    }

    @Operation(summary = "修改定时任务日志", description = "修改定时任务执行日志记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/jobLog/update")
    public int updateJobLog(@RequestBody CommonJobLogDTO entity) {
        return jobLogFeignClient.update(entity);
    }

    @Operation(summary = "删除定时任务日志", description = "根据ID列表批量删除定时任务日志", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/jobLog/delete")
    public int deleteJobLog(@RequestBody @NotNull List ids) {
        return jobLogFeignClient.deleteByIds(ids);
    }

    // ============================== 图片管理 ==============================

    @Operation(summary = "分页查询图片", description = "分页查询图片列表，支持按图片名称、分组、上传时间等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/photo/page")
    public ResponsePageEntity searchPhotoPage(@RequestBody CommonPhotoConditionDTO condition) {
        return photoFeignClient.searchByPage(condition);
    }

    @Operation(summary = "新增图片", description = "新增图片记录，关联已上传的图片文件", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/photo/insert")
    public int insertPhoto(@RequestBody CommonPhotoDTO entity) {
        return photoFeignClient.insert(entity);
    }

    @Operation(summary = "修改图片", description = "修改图片记录信息，如图片名称、所属分组等", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/photo/update")
    public int updatePhoto(@RequestBody CommonPhotoDTO entity) {
        return photoFeignClient.update(entity);
    }

    @Operation(summary = "删除图片", description = "根据ID列表批量删除图片记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/photo/delete")
    public int deletePhoto(@RequestBody @NotNull List ids) {
        return photoFeignClient.deleteByIds(ids);
    }

    // ============================== 图片分组管理 ==============================

    @Operation(summary = "分页查询图片分组", description = "分页查询图片分组列表，支持按分组名称等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/photoGroup/page")
    public ResponsePageEntity searchPhotoGroupPage(@RequestBody CommonPhotoGroupConditionDTO condition) {
        return photoGroupFeignClient.searchByPage(condition);
    }

    @Operation(summary = "新增图片分组", description = "新增图片分组，用于对图片进行分类管理", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/photoGroup/insert")
    public int insertPhotoGroup(@RequestBody CommonPhotoGroupDTO entity) {
        return photoGroupFeignClient.insert(entity);
    }

    @Operation(summary = "修改图片分组", description = "修改图片分组信息，如分组名称、排序等", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/photoGroup/update")
    public int updatePhotoGroup(@RequestBody CommonPhotoGroupDTO entity) {
        return photoGroupFeignClient.update(entity);
    }

    @Operation(summary = "删除图片分组", description = "根据ID列表批量删除图片分组", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/photoGroup/delete")
    public int deletePhotoGroup(@RequestBody @NotNull List ids) {
        return photoGroupFeignClient.deleteByIds(ids);
    }

    // ============================== 敏感词管理 ==============================

    @Operation(summary = "分页查询敏感词", description = "分页查询敏感词列表，支持按敏感词内容等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/sensitiveWord/page")
    public ResponsePageEntity searchSensitiveWordPage(@RequestBody CommonSensitiveWordConditionDTO condition) {
        return sensitiveWordFeignClient.searchByPage(condition);
    }

    @Operation(summary = "新增敏感词", description = "新增敏感词，添加到敏感词过滤库", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/sensitiveWord/insert")
    public int insertSensitiveWord(@RequestBody CommonSensitiveWordDTO entity) {
        return sensitiveWordFeignClient.insert(entity);
    }

    @Operation(summary = "修改敏感词", description = "修改敏感词内容或状态", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/sensitiveWord/update")
    public int updateSensitiveWord(@RequestBody CommonSensitiveWordDTO entity) {
        return sensitiveWordFeignClient.update(entity);
    }

    @Operation(summary = "删除敏感词", description = "根据ID列表批量删除敏感词", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/sensitiveWord/delete")
    public int deleteSensitiveWord(@RequestBody @NotNull List ids) {
        return sensitiveWordFeignClient.deleteByIds(ids);
    }

    @Operation(summary = "校验敏感词", description = "校验文本内容中是否包含敏感词，返回校验结果", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/sensitiveWord/check")
    public void checkSensitiveWord(@RequestBody CommonSensitiveWordDTO entity) {
        sensitiveWordFeignClient.checkSensitiveWord(entity);
    }

    // ============================== 短信记录管理 ==============================

    @Operation(summary = "分页查询短信记录", description = "分页查询短信发送记录，支持按手机号、发送时间、发送状态等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/smsRecord/page")
    public ResponsePageEntity searchSmsRecordPage(@RequestBody CommonSmsRecordConditionDTO condition) {
        return smsRecordFeignClient.searchByPage(condition);
    }

    @Operation(summary = "新增短信记录", description = "新增短信发送记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/smsRecord/insert")
    public int insertSmsRecord(@RequestBody SmsRecordDTO entity) {
        return smsRecordFeignClient.insert(entity);
    }

    @Operation(summary = "修改短信记录", description = "修改短信发送记录，如更新发送状态等", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/smsRecord/update")
    public int updateSmsRecord(@RequestBody SmsRecordDTO entity) {
        return smsRecordFeignClient.update(entity);
    }

    @Operation(summary = "删除短信记录", description = "根据ID列表批量删除短信发送记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/smsRecord/delete")
    public int deleteSmsRecord(@RequestBody @NotNull List ids) {
        return smsRecordFeignClient.deleteByIds(ids);
    }

    // ============================== 字典详情管理 ==============================

    @Operation(summary = "分页查询字典详情", description = "分页查询字典详情列表，支持按字典编码、字典标签等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dictDetail/page")
    public ResponsePageEntity searchDictDetailPage(@RequestBody DictDetailConditionDTO condition) {
        return dictDetailFeignClient.searchByPage(condition);
    }

    @Operation(summary = "新增字典详情", description = "新增字典详情项，如字典标签、字典值、排序等", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dictDetail/insert")
    public int insertDictDetail(@RequestBody DictDetailDTO entity) {
        return dictDetailFeignClient.insert(entity);
    }

    @Operation(summary = "修改字典详情", description = "修改字典详情项，支持更新字典标签、字典值、排序等", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dictDetail/update")
    public int updateDictDetail(@RequestBody DictDetailDTO entity) {
        return dictDetailFeignClient.update(entity);
    }

    @Operation(summary = "删除字典详情", description = "根据ID列表批量删除字典详情项", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dictDetail/delete")
    public int deleteDictDetail(@RequestBody @NotNull List ids) {
        return dictDetailFeignClient.deleteByIds(ids);
    }

    // ============================== 消息通知管理 ==============================

    @Operation(summary = "分页查询通知列表", description = "分页查询通知消息列表，支持按通知标题、推送状态、推送时间等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/notify/page")
    public ResponsePageEntity searchNotifyPage(@RequestBody CommonNotifyConditionDTO condition) {
        return notifyFeignClient.searchByPage(condition);
    }

    @Operation(summary = "全员推送通知", description = "向所有用户推送消息通知", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/notify/push/all")
    public void pushNotifyToAll(@RequestBody CommonNotifyDTO entity) {
        notifyFeignClient.pushToAll(entity);
    }

    @Operation(summary = "指定用户推送通知", description = "向指定用户推送消息通知", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/notify/push/user")
    public void pushNotifyToUser(@RequestBody CommonNotifyDTO entity) {
        notifyFeignClient.pushToUser(entity);
    }

    // ============================== 消息通知管理 CRUD ==============================

    @Operation(summary = "新增通知", description = "新增一条消息通知记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/notify/insert")
    public int insertNotify(@RequestBody CommonNotifyDTO entity) {
        return notifyFeignClient.insert(entity);
    }

    @Operation(summary = "修改通知", description = "修改指定消息通知记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/notify/update")
    public int updateNotify(@RequestBody CommonNotifyDTO entity) {
        return notifyFeignClient.update(entity);
    }

    @Operation(summary = "删除通知", description = "根据ID列表批量删除消息通知记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/notify/delete")
    public int deleteNotify(@RequestBody @NotNull List ids) {
        return notifyFeignClient.deleteByIds(ids);
    }
}
