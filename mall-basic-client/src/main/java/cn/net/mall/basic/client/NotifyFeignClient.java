package cn.net.mall.basic.client;

import cn.net.mall.basic.dto.CommonNotifyConditionDTO;
import cn.net.mall.basic.dto.CommonNotifyDTO;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * [Service] Feign 客户端
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>mall-admin-api（管理后台）— 消息通知管理</li>
 * </ul>
 * <p>
 * 映射至 mall-message 服务
 *
 * @date 2025/7/3
 */
@FeignClient(value = "mall-message-api", contextId = "notifyFeignClient")
public interface NotifyFeignClient {

    @Operation(summary = "通过id查询通知信息", description = "内部Feign调用：根据主键查询消息通知")
    @GetMapping("/v1/message/notify/findById")
    Object findById(@RequestParam("id") Long id);

    @Operation(summary = "分页查询通知列表（管理端）", description = "分页查询通知消息列表，支持按通知标题、推送状态、推送时间等条件筛选")
    @PostMapping("/v1/message/notify/searchByPage")
    ResponsePageEntity<CommonNotifyDTO> searchByPage(@RequestBody CommonNotifyConditionDTO condition);

    @Operation(summary = "全员推送通知（管理端）", description = "向所有用户推送消息通知")
    @PostMapping("/v1/message/notify/push/all")
    void pushToAll(@RequestBody CommonNotifyDTO notify);

    @Operation(summary = "指定用户推送通知（管理端）", description = "向指定用户推送消息通知")
    @PostMapping("/v1/message/notify/push/user")
    void pushToUser(@RequestBody CommonNotifyDTO notify);

    @Operation(summary = "新增通知（管理端）", description = "新增一条消息通知记录")
    @PostMapping("/v1/message/notify/insert")
    int insert(@RequestBody Object entity);

    @Operation(summary = "修改通知（管理端）", description = "修改一条已有的消息通知记录")
    @PostMapping("/v1/message/notify/update")
    int update(@RequestBody Object entity);

    @Operation(summary = "删除通知（管理端）", description = "根据 ID 列表批量删除通知记录")
    @PostMapping("/v1/message/notify/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}
