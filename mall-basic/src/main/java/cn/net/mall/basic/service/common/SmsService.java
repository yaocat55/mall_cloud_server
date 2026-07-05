package cn.net.mall.basic.service.common;

import cn.hutool.core.util.BooleanUtil;
import cn.net.mall.admin.client.UserFeignClient;
import cn.net.mall.admin.dto.UserDTO;
import cn.net.mall.basic.dto.SendCodeDTO;
import cn.net.mall.basic.entity.common.CommonSmsRecordEntity;
import cn.net.mall.basic.mapper.common.CommonSmsRecordMapper;
import cn.net.mall.basic.util.SmsUtil;
import cn.net.mall.enums.SmsTypeEnum;
import cn.net.mall.util.AssertUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @date 2025/5/17 14:10
 */
@Service
public class SmsService {


    @Value("${aliyun.sms.registerTemplateCode}")
    private String registerTemplateCode;

    @Value("${aliyun.sms.loginTemplateCode}")
    private String loginTemplateCode;

    @Value("${aliyun.sms.offTestSms:true}")
    private Boolean offTestSms;

    private final UserFeignClient userFeignClient;
    private final SmsUtil smsUtil;
    private final CommonSmsRecordMapper commonSmsRecordMapper;

    public SmsService(UserFeignClient userFeignClient, SmsUtil smsUtil, CommonSmsRecordMapper commonSmsRecordMapper) {
        this.userFeignClient = userFeignClient;
        this.smsUtil = smsUtil;
        this.commonSmsRecordMapper = commonSmsRecordMapper;
    }

    public void sendSmsCode(SendCodeDTO sendCodeDTO) {
        String phone = sendCodeDTO.getPhone();
        Integer type = sendCodeDTO.getType();

        // 生成6位随机验证码
        String code = generateSmsVerifyCode();

        // 保存验证码
        CommonSmsRecordEntity verificationCode = new CommonSmsRecordEntity();
        verificationCode.setPhone(phone);
        verificationCode.setSmsCode(code);
        verificationCode.setType(sendCodeDTO.getType());
        verificationCode.setExpireSecond(60); // 10分钟有效期
        verificationCode.setSendTime(new Date());
        verificationCode.setCreateTime(new Date());
        verificationCode.setCreateUserId(1L);
        verificationCode.setCreateUserName("游客");

        commonSmsRecordMapper.deleteByPhoneAndType(phone, type); // 删除旧验证码
        commonSmsRecordMapper.insert(verificationCode);

        if (BooleanUtil.isTrue(offTestSms)) {
            return;
        }

        // 发送短信
        String templateParam = "{\"code\":\"" + code + "\"}";
        String templateCode = getSmsTemplateCode(type);
        smsUtil.sendSms(phone, templateCode, templateParam);
    }

    /**
     * 生成6位随机验证码
     */
    private String generateSmsVerifyCode() {
        if (BooleanUtil.isTrue(offTestSms)) {
            return "123456";
        }
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    /**
     * 获取短信模板代码
     */
    private String getSmsTemplateCode(Integer type) {
        switch (type) {
            case 1:
                return registerTemplateCode;
            case 2:
                return registerTemplateCode;
            case 3:
                return loginTemplateCode;
            default:
                return "SMS_DEFAULT_TEMPLATE";
        }
    }
}