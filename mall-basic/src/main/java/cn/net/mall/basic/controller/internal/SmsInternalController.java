package cn.net.mall.basic.controller.internal;

import cn.net.mall.basic.dto.SendCodeDTO;
import cn.net.mall.basic.service.common.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "内部服务-短信", description = "内部微服务：mall-auth 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/sms")
@AllArgsConstructor
public class SmsInternalController {
    private final SmsService smsService;
    @Operation(summary = "发送短信验证码", description = "内部服务：由 mall-auth 通过 Feign 调用，发送验证码短信")
    @PostMapping("/sendSmsCode")
    public void sendSmsCode(@RequestBody @Validated SendCodeDTO sendCodeDTO) {
        smsService.sendSmsCode(sendCodeDTO);
    }
}
