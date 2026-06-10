package cn.net.mall.auth.authenication;

import cn.net.mall.auth.entity.auth.UserConditionEntity;
import cn.net.mall.auth.entity.auth.UserEntity;
import cn.net.mall.basic.client.SmsRecordFeignClient;
import cn.net.mall.basic.dto.SmsRecordConditionDTO;
import cn.net.mall.basic.dto.SmsRecordDTO;
import cn.net.mall.enums.SmsTypeEnum;
import cn.net.mall.auth.mapper.auth.UserMapper;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.util.RandomUtil;
import cn.net.mall.util.RedisUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;

import java.util.List;

import static cn.net.mall.util.SmsKeyUtil.getSmsCodePrefixKey;

/**
 * @date 2024/11/8 下午4:08
 */
public class SmsAuthenticationProvider implements AuthenticationProvider {
    private static final String DEFAULT_NICK_NAME = "手机号注册用户";

    private final UserDetailsService userDetailsService;
    private final UserMapper userMapper;
    private final SmsRecordFeignClient smsRecordFeignClient;

    public SmsAuthenticationProvider(UserDetailsService userDetailsService, UserMapper userMapper, SmsRecordFeignClient smsRecordFeignClient) {
        this.userDetailsService = userDetailsService;
        this.userMapper = userMapper;
        this.smsRecordFeignClient = smsRecordFeignClient;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取手机号
        String phone = (String) authentication.getPrincipal();
        // 获取验证码
        String captcha = (String) authentication.getCredentials();
        SmsRecordConditionDTO smsRecordConditionDTO = new SmsRecordConditionDTO();
        smsRecordConditionDTO.setPhone(phone);
        smsRecordConditionDTO.setSmsTypeEnum(SmsTypeEnum.LOGIN);
        SmsRecordDTO smsRecord = smsRecordFeignClient.findSmsRecord(smsRecordConditionDTO);
        AssertUtil.notNull(smsRecord, "该短信验证码已失效");
        AssertUtil.isTrue(smsRecord.getSmsCode().trim().equals(captcha), "短信验证码错误");

        UserConditionEntity userConditionEntity = new UserConditionEntity();
        userConditionEntity.setPhone(phone);
        List<UserEntity> userEntities = userMapper.searchByCondition(userConditionEntity);

        UserEntity userEntity;
        if (CollectionUtils.isEmpty(userEntities)) {
            userEntity = FillUserUtil.mockCurrentUser(() -> registerUser(phone));
        } else {
            userEntity = userEntities.get(0);
        }

        // 验证用户信息
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEntity.getUserName());
        if (userDetails == null) {
            throw new BadCredentialsException("未找到对应的用户,请先注册");
        }

        // 创建已认证的Token
        return new SmsAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private UserEntity registerUser(String phone) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(getUsername());
        userEntity.setNickName(buildPhoneNickname(phone));
        userEntity.setPhone(phone);
        userMapper.insert(userEntity);
        return userEntity;
    }

    private String getUsername() {
        return RandomUtil.getSixBitRandom();
    }
    
    private String buildPhoneNickname(String phone) {
        if (!StringUtils.hasLength(phone)) {
            return DEFAULT_NICK_NAME;
        }
        int len = phone.length();
        if (len <= 7) {
            return phone;
        }
        String prefix = phone.substring(0, 3);
        String suffix = phone.substring(len - 4);
        return prefix + "..." + suffix;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
