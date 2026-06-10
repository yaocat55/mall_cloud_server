package cn.net.mall.message.service;

import cn.net.mall.auth.client.UserFeignClient;
import cn.net.mall.auth.dto.UserDTO;
import cn.net.mall.message.entity.CommonNotifyEntity;
import cn.net.mall.message.mapper.CommonNotifyMapper;
import cn.net.mall.util.FillUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class MessagePushService {

    private static final Long BROADCAST_USER_ID = 0L;

    private final SimpMessagingTemplate messagingTemplate;
    private final CommonNotifyMapper commonNotifyMapper;
    private final UserFeignClient userFeignClient;

    public MessagePushService(SimpMessagingTemplate messagingTemplate,
                              CommonNotifyMapper commonNotifyMapper,
                              UserFeignClient userFeignClient) {
        this.messagingTemplate = messagingTemplate;
        this.commonNotifyMapper = commonNotifyMapper;
        this.userFeignClient = userFeignClient;
    }

    public void pushToAll(CommonNotifyEntity notify) {
        notify.setToUserId(BROADCAST_USER_ID);
        notify.setIsPush(1);
        FillUserUtil.fillCreateUserInfo(notify);
        commonNotifyMapper.insert(notify);
        boolean success = false;
        try {
            messagingTemplate.convertAndSend("/topic/notify", notify);
            success = true;
        } catch (Exception e) {
            log.error("message push failed, userId={}, type={}, title={}, time={}", BROADCAST_USER_ID, notify.getType(), notify.getTitle(), LocalDateTime.now(), e);
            return;
        }
        log.info("message push success, userId={}, type={}, title={}, time={}, success={}", BROADCAST_USER_ID, notify.getType(), notify.getTitle(), LocalDateTime.now(), success);
    }

    public void pushToUser(CommonNotifyEntity notify) {
        Long userId = notify.getToUserId();
        if (Objects.isNull(userId)) {
            log.warn("message push skipped, userId=null, type={}, title={}, time={}", notify.getType(), notify.getTitle(), LocalDateTime.now());
            return;
        }
        notify.setIsPush(1);
        FillUserUtil.fillCreateUserInfo(notify);
        commonNotifyMapper.insert(notify);
        List<UserDTO> list = userFeignClient.findByIds(Collections.singletonList(userId));
        if (Objects.nonNull(list) && !list.isEmpty()) {
            String username = list.get(0).getUserName();
            boolean success = false;
            try {
                messagingTemplate.convertAndSendToUser(username, "/queue/notify", notify);
                success = true;
            } catch (Exception e) {
                log.error("message push failed, userId={}, username={}, type={}, title={}, time={}", userId, username, notify.getType(), notify.getTitle(), LocalDateTime.now(), e);
                return;
            }
            log.info("message push success, userId={}, username={}, type={}, title={}, time={}, success={}", userId, username, notify.getType(), notify.getTitle(), LocalDateTime.now(), success);
            return;
        }
        log.warn("message push skipped, userId={}, type={}, title={}, time={}, reason=no_user", userId, notify.getType(), notify.getTitle(), LocalDateTime.now());
    }
}
