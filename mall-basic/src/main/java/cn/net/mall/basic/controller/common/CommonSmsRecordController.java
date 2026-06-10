package cn.net.mall.basic.controller.common;

import cn.net.mall.annotation.NoLogin;
import cn.net.mall.basic.dto.SmsRecordConditionDTO;
import cn.net.mall.basic.dto.SmsRecordDTO;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.basic.entity.common.CommonSmsRecordConditionEntity;
import cn.net.mall.basic.entity.common.CommonSmsRecordEntity;
import cn.net.mall.basic.service.common.CommonSmsRecordService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 短信发送记录 接口层
 *
 * @date 2024-11-08 13:03:15
 */
@RestController
@RequestMapping("/v1/commonSmsRecord")
public class CommonSmsRecordController {

    private final CommonSmsRecordService commonSmsRecordService;

    public CommonSmsRecordController(CommonSmsRecordService commonSmsRecordService) {
        this.commonSmsRecordService = commonSmsRecordService;
    }


    /**
     * 查询短信发送记录
     *
     * @param smsRecordConditionDTO 查询条件
     * @return 短信发送记录
     */
    @NoLogin
    @Operation(summary = "查询短信发送记录", description = "查询短信发送记录")
    @PostMapping(value = "/findSmsRecord")
    public SmsRecordDTO findSmsRecord(@RequestBody @Validated SmsRecordConditionDTO smsRecordConditionDTO) {
        return commonSmsRecordService.findSmsRecord(smsRecordConditionDTO);
    }

    /**
     * 通过id查询短信发送记录信息
     *
     * @param id 系统ID
     * @return 短信发送记录信息
     */
    @GetMapping("/findById")
    public CommonSmsRecordEntity findById(Long id) {
        return commonSmsRecordService.findById(id);
    }

    /**
     * 根据条件查询短信发送记录列表
     *
     * @param commonSmsRecordConditionEntity 条件
     * @return 短信发送记录列表
     */
    @PostMapping("/searchByPage")
    public ResponsePageEntity<CommonSmsRecordEntity> searchByPage(@RequestBody CommonSmsRecordConditionEntity commonSmsRecordConditionEntity) {
        return commonSmsRecordService.searchByPage(commonSmsRecordConditionEntity);
    }


    /**
     * 添加短信发送记录
     *
     * @param commonSmsRecordEntity 短信发送记录实体
     * @return 影响行数
     */
    @PostMapping("/insert")
    public int insert(@RequestBody CommonSmsRecordEntity commonSmsRecordEntity) {
        return commonSmsRecordService.insert(commonSmsRecordEntity);
    }

    /**
     * 修改短信发送记录
     *
     * @param commonSmsRecordEntity 短信发送记录实体
     * @return 影响行数
     */
    @PostMapping("/update")
    public int update(@RequestBody CommonSmsRecordEntity commonSmsRecordEntity) {
        return commonSmsRecordService.update(commonSmsRecordEntity);
    }

    /**
     * 批量删除短信发送记录
     *
     * @param ids 短信发送记录ID集合
     * @return 影响行数
     */
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return commonSmsRecordService.deleteByIds(ids);
    }
}
