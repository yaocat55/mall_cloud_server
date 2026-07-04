package cn.net.mall.basic.client;

import cn.net.mall.basic.dto.CommonSmsRecordConditionDTO;
import cn.net.mall.basic.dto.SmsRecordConditionDTO;
import cn.net.mall.basic.dto.SmsRecordDTO;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static cn.net.mall.basic.constant.AppConstant.BASIC_SERVICE_NAME;

/**
 * [Service] Feign 客户端
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>mall-auth（权限服务）— 查询短信发送记录</li>
 *   <li>mall-admin-api（管理后台）— 短信发送记录管理</li>
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

    @Operation(summary = "分页查询短信记录（管理端）", description = "分页查询短信发送记录，支持按手机号、发送时间、发送状态等条件筛选")
    @PostMapping("/v1/commonSmsRecord/searchByPage")
    ResponsePageEntity<SmsRecordDTO> searchByPage(@RequestBody CommonSmsRecordConditionDTO condition);

    @Operation(summary = "新增短信记录（管理端）", description = "新增短信发送记录")
    @PostMapping("/v1/commonSmsRecord/insert")
    int insert(@RequestBody SmsRecordDTO entity);

    @Operation(summary = "修改短信记录（管理端）", description = "修改短信发送记录，如更新发送状态等")
    @PostMapping("/v1/commonSmsRecord/update")
    int update(@RequestBody SmsRecordDTO entity);

    @Operation(summary = "删除短信记录（管理端）", description = "根据ID列表批量删除短信发送记录")
    @PostMapping("/v1/commonSmsRecord/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}
