package cn.net.mall.message.service;

import cn.net.mall.admin.client.UserFeignClient;
import cn.net.mall.admin.dto.UserDTO;
import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.message.entity.CommonNotifyEntity;
import cn.net.mall.message.mapper.CommonNotifyMapper;
import cn.net.mall.util.FillUserUtil;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MessagePushServiceTest {

    private final JwtUserEntity testUser = new JwtUserEntity(1L, "admin", null, Collections.emptyList(), Collections.emptyList());

    @Test
    public void should_send_broadcast_when_pushToAll() {
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        CommonNotifyMapper mapper = mock(CommonNotifyMapper.class);
        UserFeignClient userFeignClient = mock(UserFeignClient.class);
        MessagePushService service = new MessagePushService(messagingTemplate, mapper, userFeignClient);

        CommonNotifyEntity notify = new CommonNotifyEntity();
        notify.setTitle("系统公告");
        notify.setContent("全员通知内容");

        FillUserUtil.mockUser(() -> {
            service.pushToAll(notify);
            return null;
        }, testUser);

        verify(mapper, times(1)).insert(any(CommonNotifyEntity.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/notify"), any(Object.class));
    }

    @Test
    public void should_send_to_user_when_pushToUser() {
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        CommonNotifyMapper mapper = mock(CommonNotifyMapper.class);
        UserFeignClient userFeignClient = mock(UserFeignClient.class);
        MessagePushService service = new MessagePushService(messagingTemplate, mapper, userFeignClient);

        CommonNotifyEntity notify = new CommonNotifyEntity();
        notify.setTitle("个人通知");
        notify.setContent("您的订单已发货");
        notify.setToUserId(1001L);

        UserDTO userDTO = new UserDTO();
        userDTO.setUserName("jack");
        when(userFeignClient.findByIds(Collections.singletonList(1001L))).thenReturn(Collections.singletonList(userDTO));

        FillUserUtil.mockUser(() -> {
            service.pushToUser(notify);
            return null;
        }, testUser);

        verify(mapper, times(1)).insert(any(CommonNotifyEntity.class));
        verify(messagingTemplate, times(1)).convertAndSendToUser(eq("jack"), eq("/queue/notify"), any());
    }

    @Test
    public void should_skip_when_user_id_null() {
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        CommonNotifyMapper mapper = mock(CommonNotifyMapper.class);
        UserFeignClient userFeignClient = mock(UserFeignClient.class);
        MessagePushService service = new MessagePushService(messagingTemplate, mapper, userFeignClient);

        CommonNotifyEntity notify = new CommonNotifyEntity();
        notify.setTitle("个人通知");
        notify.setContent("内容");

        service.pushToUser(notify);

        verifyNoInteractions(messagingTemplate, mapper, userFeignClient);
    }
}
