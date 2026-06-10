package cn.net.mall.basic.client;

import cn.net.mall.basic.dto.SmsRecordConditionDTO;
import cn.net.mall.basic.dto.SmsRecordDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static cn.net.mall.basic.constant.AppConstant.BASIC_SERVICE_NAME;

/**
 * 查询短信发送记录
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
    @Operation(summary = "查询短信发送记录", description = "查询短信发送记录")
    @PostMapping(value = "/v1/commonSmsRecord/findSmsRecord")
    SmsRecordDTO findSmsRecord(@RequestBody SmsRecordConditionDTO smsRecordConditionDTO);
}
