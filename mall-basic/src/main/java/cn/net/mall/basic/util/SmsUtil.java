package cn.net.mall.basic.util;

import cn.net.mall.exception.BusinessException;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 短信工具类
 */
@Slf4j
@Component
public class SmsUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(SmsUtil.class);
    
    @Value("${sms.enabled:false}")
    private boolean enabled;

    @Value("${aliyun.sms.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.sms.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.sms.sign-name}")
    private String signName;

    @Value("${aliyun.sms.region-id}")
    private String regionId;

    /**
     * 发送短信
     *
     * @param phoneNumber   手机号
     * @param templateCode  模板代码
     * @param templateParam 模板参数，JSON格式
     */
    public void sendSms(String phoneNumber, String templateCode, String templateParam) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", templateParam);

        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info("短信发送结果：{}", response.getData());

            // 解析响应结果
            if (!response.getData().contains("\"Code\":\"OK\"")) {
                log.error("短信发送失败：{}", response.getData());
                throw new BusinessException("短信发送失败，请稍后再试");
            }
        } catch (ClientException e) {
            log.error("短信发送异常：", e);
            throw new BusinessException("短信发送失败，请稍后再试");
        }
    }
    
    /**
     * 发送短信
     * @param phone 手机号
     * @param content 内容
     * @return 是否发送成功
     */
    public boolean sendSms(String phone, String content) {
        if (!enabled) {
            // 测试环境不发送真实短信，直接打印日志
            logger.info("发送短信到 {}: {}", phone, content);
            return true;
        }
        
        try {
            // TODO: 实现真实的短信发送逻辑，调用第三方短信服务
            // 这里仅作为示例，实际项目中需要替换为真实的短信发送实现
            logger.info("发送短信到 {}: {}", phone, content);
            return true;
        } catch (Exception e) {
            logger.error("发送短信失败: {}", e.getMessage(), e);
            return false;
        }
    }
}