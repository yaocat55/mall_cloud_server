package cn.net.mall.basic.controller.mobile;

import cn.net.mall.basic.dto.SendCodeDTO;
import cn.net.mall.basic.service.common.SmsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短信接口
 *
 * @date 2026/1/21 17:00
 */
@Tag(name = "短信接口", description = "短信接口")
@RestController
@RequestMapping("/v1/mobile/sms")
@RequiredArgsConstructor
public class MobileSmsController {

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