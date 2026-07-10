package cn.net.mall.basic.service.common;

import cn.hutool.core.util.BooleanUtil;
import cn.net.mall.basic.dto.SendCodeDTO;
import cn.net.mall.basic.entity.common.CommonSmsRecordEntity;
import cn.net.mall.basic.mapper.common.CommonSmsRecordMapper;
import cn.net.mall.basic.util.SmsUtil;
import cn.net.mall.enums.SmsTypeEnum;
import cn.net.mall.redis.RedisUtil;
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

    private static final String SMS_LIMIT_PREFIX = "sms:limit:";
    private static final int SMS_LIMIT_SECONDS = 60;

    @Value("${aliyun.sms.registerTemplateCode}")
    private String registerTemplateCode;

    @Value("${aliyun.sms.loginTemplateCode}")
    private String loginTemplateCode;

    @Value("${aliyun.sms.offTestSms:true}")
    private Boolean offTestSms;

    private final SmsUtil smsUtil;
    private final CommonSmsRecordMapper commonSmsRecordMapper;
    private final RedisUtil redisUtil;

    public SmsService(SmsUtil smsUtil, CommonSmsRecordMapper commonSmsRecordMapper, RedisUtil redisUtil) {
        this.smsUtil = smsUtil;
        this.commonSmsRecordMapper = commonSmsRecordMapper;
        this.redisUtil = redisUtil;
    }

    public void sendSmsCode(SendCodeDTO sendCodeDTO) {
        String phone = sendCodeDTO.getPhone();

        // 频率限制：同一手机号 60 秒内只能发一次
        String limitKey = SMS_LIMIT_PREFIX + phone;
        String exists = redisUtil.get(limitKey);
        AssertUtil.isNull(exists, "发送过于频繁，请稍后再试");
        redisUtil.set(limitKey, "1", SMS_LIMIT_SECONDS);
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