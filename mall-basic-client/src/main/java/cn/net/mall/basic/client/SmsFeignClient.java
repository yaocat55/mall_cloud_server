package cn.net.mall.basic.client;

import cn.net.mall.basic.dto.SendCodeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static cn.net.mall.basic.constant.AppConstant.BASIC_SERVICE_NAME;

/**
 * 发送短信
 *
 * @date 2025/5/17 14:42
 */
@FeignClient(value = BASIC_SERVICE_NAME, contextId = "smsFeignClient")
public interface SmsFeignClient {

    /**
     * 发送验证码
     *
     * @param sendCodeDTO 发送验证码请求
     * @return 发送结果
     */
    @PostMapping("/v1/sms/sendSmsCode")
    void sendSmsCode(@RequestBody @Validated SendCodeDTO sendCodeDTO);
}
