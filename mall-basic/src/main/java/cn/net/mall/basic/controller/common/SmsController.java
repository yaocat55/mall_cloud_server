package cn.net.mall.basic.controller.common;

import cn.net.mall.basic.dto.SendCodeDTO;
import cn.net.mall.basic.service.common.SmsService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @date 2025/5/17 14:08
 */
@AllArgsConstructor
@RestController
@RequestMapping("/v1/sms")
public class SmsController {

    private final SmsService smsService;

    /**
     * 发送验证码
     *
     * @param sendCodeDTO 发送验证码请求
     * @return 发送结果
     */
    @PostMapping("/sendSmsCode")
    public void sendSmsCode(@RequestBody @Validated SendCodeDTO sendCodeDTO) {
        smsService.sendSmsCode(sendCodeDTO);
    }
}