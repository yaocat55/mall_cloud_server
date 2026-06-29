package cn.net.mall.basic.client;

import cn.net.mall.basic.dto.SmsRecordConditionDTO;
import cn.net.mall.basic.dto.SmsRecordDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static cn.net.mall.basic.constant.AppConstant.BASIC_SERVICE_NAME;

/**
 * [Service] Feign 客户端
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>mall-auth（权限服务）— 查询短信发送记录</li>
 * </ul>
 *
 * @date 2025/5/17 12:24
 */
@FeignClient(value = BASIC_SERVICE_NAME, contextId = "smsRecordFeignClient")
public interface SmsRecordFeignClient {

    /**
     * 查询短信发送记录
     *
     * @param smsRecordConditionDTO 查询条件
     * @return 短信发送记录
     */
    @Operation(summary = "查询短信发送记录", description = "内部Feign调用：由mall-auth发起，查询短信发送记录")
    @PostMapping(value = "/v1/internal/smsRecord/findSmsRecord")
    SmsRecordDTO findSmsRecord(@RequestBody SmsRecordConditionDTO smsRecordConditionDTO);
}
