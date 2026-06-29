package cn.net.mall.basic.controller.internal;

import cn.net.mall.basic.dto.SmsRecordConditionDTO;
import cn.net.mall.basic.dto.SmsRecordDTO;
import cn.net.mall.basic.service.common.CommonSmsRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "内部服务-短信记录", description = "内部微服务：mall-auth 通过 Feign 调用")
@RestController
@RequestMapping("/v1/commonSmsRecord")
public class SmsRecordInternalController {
    private final CommonSmsRecordService commonSmsRecordService;
    public SmsRecordInternalController(CommonSmsRecordService commonSmsRecordService) {
        this.commonSmsRecordService = commonSmsRecordService;
    }
    @Operation(summary = "查询短信发送记录", description = "内部服务：由 mall-auth 通过 Feign 调用，根据条件查询短信发送状态")
    @PostMapping("/findSmsRecord")
    public SmsRecordDTO findSmsRecord(@RequestBody @Validated SmsRecordConditionDTO dto) {
        return commonSmsRecordService.findSmsRecord(dto);
    }
}
