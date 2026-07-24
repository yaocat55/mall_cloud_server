package cn.net.mall.message.controller;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.message.entity.CommonNotifyConditionEntity;
import cn.net.mall.message.entity.CommonNotifyEntity;
import cn.net.mall.message.service.CommonNotifyService;
import cn.net.mall.message.service.MessagePushService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

/**
 * 内部通知接口.
 *
 * <p>Feign 调用，返回裸 DTO。</p>
 */
@Tag(name = "内部服务-通知", description = "内部微服务 Feign 调用")
@RestController
@RequestMapping("/v1/internal/message/notify")
public class MessageNotifyInternalController {

    private final CommonNotifyService commonNotifyService;
    private final MessagePushService messagePushService;

    public MessageNotifyInternalController(CommonNotifyService commonNotifyService,
                                           MessagePushService messagePushService) {
        this.commonNotifyService = commonNotifyService;
        this.messagePushService = messagePushService;
    }

    @Operation(summary = "通过id查询通知信息", description = "内部Feign调用：根据主键查询消息通知")
    @GetMapping("/findById")
    public CommonNotifyEntity findById(@Parameter(description = "通知ID") @RequestParam("id") Long id) {
        return commonNotifyService.findById(id);
    }

    @Operation(summary = "分页查询通知列表", description = "内部Feign调用：分页查询通知列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<CommonNotifyEntity> searchByPage(@RequestBody CommonNotifyConditionEntity condition) {
        return commonNotifyService.searchByPage(condition);
    }

    @Operation(summary = "全员推送通知", description = "内部Feign调用：全员推送")
    @PostMapping("/push/all")
    public void pushToAll(@RequestBody CommonNotifyEntity notify) {
        messagePushService.pushToAll(notify);
    }

    @Operation(summary = "指定用户推送通知", description = "内部Feign调用：指定用户推送")
    @PostMapping("/push/user")
    public void pushToUser(@RequestBody CommonNotifyEntity notify) {
        messagePushService.pushToUser(notify);
    }

    @Operation(summary = "新增通知", description = "内部Feign调用：新增通知")
    @PostMapping("/insert")
    public int insert(@RequestBody CommonNotifyEntity entity) {
        return commonNotifyService.insert(entity);
    }

    @Operation(summary = "修改通知", description = "内部Feign调用：修改通知")
    @PostMapping("/update")
    public int update(@RequestBody CommonNotifyEntity entity) {
        return commonNotifyService.update(entity);
    }

    @Operation(summary = "删除通知", description = "内部Feign调用：删除通知")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody Long id) {
        return commonNotifyService.deleteById(id);
    }
}
