package cn.net.mall.message.controller;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.message.entity.CommonNotifyConditionEntity;
import cn.net.mall.message.entity.CommonNotifyEntity;
import cn.net.mall.message.service.CommonNotifyService;
import cn.net.mall.message.service.MessagePushService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name = "消息通知", description = "消息通知接口")
@RestController
@RequestMapping("/v1/message/notify")
public class MessageNotifyController {

    private static final String DEFAULT_TEST_TITLE = "测试通知";
    private static final String DEFAULT_TEST_CONTENT = "测试内容";

    private final CommonNotifyService commonNotifyService;
    private final MessagePushService messagePushService;

    public MessageNotifyController(CommonNotifyService commonNotifyService,
                                   MessagePushService messagePushService) {
        this.commonNotifyService = commonNotifyService;
        this.messagePushService = messagePushService;
    }

    @Operation(summary = "通过id查询通知信息", description = "通过id查询通知信息")
    @GetMapping("/findById")
    public CommonNotifyEntity findById(@RequestParam("id") Long id) {
        return commonNotifyService.findById(id);
    }

    @Operation(summary = "根据条件查询通知列表", description = "根据条件查询通知列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<CommonNotifyEntity> searchByPage(@RequestBody CommonNotifyConditionEntity condition) {
        return commonNotifyService.searchByPage(condition);
    }

    @Operation(summary = "全员推送通知", description = "全员推送通知")
    @PostMapping("/push/all")
    public void pushToAll(@RequestBody CommonNotifyEntity notify) {
        messagePushService.pushToAll(notify);
    }

    @Operation(summary = "指定用户推送通知", description = "指定用户推送通知")
    @PostMapping("/push/user")
    public void pushToUser(@RequestBody CommonNotifyEntity notify) {
        messagePushService.pushToUser(notify);
    }

    @Operation(summary = "测试推送通知", description = "测试推送通知")
    @GetMapping("/test/push")
    public void testPush(@RequestParam(value = "toUserId", required = false) Long toUserId,
                         @RequestParam(value = "title", required = false) String title,
                         @RequestParam(value = "content", required = false) String content) {
        CommonNotifyEntity notify = new CommonNotifyEntity();
        notify.setTitle(StringUtils.hasText(title) ? title : DEFAULT_TEST_TITLE);
        notify.setContent(StringUtils.hasText(content) ? content : DEFAULT_TEST_CONTENT);
        if (toUserId == null) {
            messagePushService.pushToAll(notify);
            return;
        }
        notify.setToUserId(toUserId);
        messagePushService.pushToUser(notify);
    }
}
