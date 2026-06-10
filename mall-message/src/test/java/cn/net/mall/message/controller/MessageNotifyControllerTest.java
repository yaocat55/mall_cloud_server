package cn.net.mall.message.controller;

import cn.net.mall.message.entity.CommonNotifyEntity;
import cn.net.mall.message.service.CommonNotifyService;
import cn.net.mall.message.service.MessagePushService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MessageNotifyControllerTest {

    @Test
    public void should_push_to_all_when_user_id_null() {
        CommonNotifyService commonNotifyService = mock(CommonNotifyService.class);
        MessagePushService messagePushService = mock(MessagePushService.class);
        MessageNotifyController controller = new MessageNotifyController(commonNotifyService, messagePushService);

        controller.testPush(null, null, null);

        ArgumentCaptor<CommonNotifyEntity> captor = ArgumentCaptor.forClass(CommonNotifyEntity.class);
        verify(messagePushService, times(1)).pushToAll(captor.capture());
        CommonNotifyEntity notify = captor.getValue();
        assertEquals("测试通知", notify.getTitle());
        assertEquals("测试内容", notify.getContent());
    }

    @Test
    public void should_push_to_user_when_user_id_present() {
        CommonNotifyService commonNotifyService = mock(CommonNotifyService.class);
        MessagePushService messagePushService = mock(MessagePushService.class);
        MessageNotifyController controller = new MessageNotifyController(commonNotifyService, messagePushService);

        controller.testPush(1001L, "标题", "内容");

        ArgumentCaptor<CommonNotifyEntity> captor = ArgumentCaptor.forClass(CommonNotifyEntity.class);
        verify(messagePushService, times(1)).pushToUser(captor.capture());
        CommonNotifyEntity notify = captor.getValue();
        assertEquals(1001L, notify.getToUserId());
        assertEquals("标题", notify.getTitle());
        assertEquals("内容", notify.getContent());
    }

    @Test
    public void should_use_default_values_when_blank_input() {
        CommonNotifyService commonNotifyService = mock(CommonNotifyService.class);
        MessagePushService messagePushService = mock(MessagePushService.class);
        MessageNotifyController controller = new MessageNotifyController(commonNotifyService, messagePushService);

        controller.testPush(null, " ", "");

        ArgumentCaptor<CommonNotifyEntity> captor = ArgumentCaptor.forClass(CommonNotifyEntity.class);
        verify(messagePushService, times(1)).pushToAll(captor.capture());
        CommonNotifyEntity notify = captor.getValue();
        assertEquals("测试通知", notify.getTitle());
        assertEquals("测试内容", notify.getContent());
    }

    @Test
    public void should_keep_large_content_when_present() {
        CommonNotifyService commonNotifyService = mock(CommonNotifyService.class);
        MessagePushService messagePushService = mock(MessagePushService.class);
        MessageNotifyController controller = new MessageNotifyController(commonNotifyService, messagePushService);

        String largeContent = "a".repeat(5000);
        controller.testPush(1002L, null, largeContent);

        ArgumentCaptor<CommonNotifyEntity> captor = ArgumentCaptor.forClass(CommonNotifyEntity.class);
        verify(messagePushService, times(1)).pushToUser(captor.capture());
        CommonNotifyEntity notify = captor.getValue();
        assertEquals(1002L, notify.getToUserId());
        assertEquals("测试通知", notify.getTitle());
        assertEquals(largeContent, notify.getContent());
    }
}
