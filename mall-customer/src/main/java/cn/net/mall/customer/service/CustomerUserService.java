package cn.net.mall.customer.service;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.basic.client.SmsRecordFeignClient;
import cn.net.mall.basic.dto.SmsRecordConditionDTO;
import cn.net.mall.basic.dto.SmsRecordDTO;
import cn.net.mall.customer.dto.*;
import cn.net.mall.customer.entity.CustomerUserEntity;
import cn.net.mall.customer.mapper.CustomerUserMapper;
import cn.net.mall.enums.SmsTypeEnum;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.redis.RedisUtil;
import cn.net.mall.redis.UserTokenHelper;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.TokenUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class CustomerUserService {
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";

    // ========== C端 token 管理（已废弃，改用 JWT 过期机制） ==========
    // C 端用户登出时前端清除本地 token 即可，无需服务端做黑名单。
    // Admin 端才需要 Redis 黑名单来做强制踢人/权限变更。
    // 保留代码片段以便后续如有 C 端黑名单需求可快速恢复。

    // private static final String BLACKLIST_PREFIX = "blacklist:";
    // private static final String USER_JTI_PREFIX = "user_token_jti:";

    private static final String TOKEN_SECRET_DEFAULT = "123456test";

    @Value("${mall.mgt.tokenExpireTimeInRecord:3600}")
    private int tokenExpireTimeInRecord;

    private final CustomerUserMapper customerUserMapper;
    private final RedisUtil redisUtil;
    private final UserTokenHelper userTokenHelper;
    private final PasswordEncoder passwordEncoder;
    private final SmsRecordFeignClient smsRecordFeignClient;

    public CustomerUserService(CustomerUserMapper customerUserMapper, RedisUtil redisUtil,
                               UserTokenHelper userTokenHelper, PasswordEncoder passwordEncoder,
                               SmsRecordFeignClient smsRecordFeignClient) {
        this.customerUserMapper = customerUserMapper;
        this.redisUtil = redisUtil;
        this.userTokenHelper = userTokenHelper;
        this.passwordEncoder = passwordEncoder;
        this.smsRecordFeignClient = smsRecordFeignClient;
    }

    public MemberDTO register(MemberRegisterDTO registerDTO) {
        String captchaKey = CAPTCHA_KEY_PREFIX + registerDTO.getUuid();
        String captcha = redisUtil.get(captchaKey);
        AssertUtil.hasLength(captcha, "验证码已失效");
        AssertUtil.isTrue(captcha.trim().equalsIgnoreCase(registerDTO.getCode().trim()), "验证码错误");
        redisUtil.del(captchaKey);

        boolean isCodeValid = verifySmsCode(registerDTO.getPhone(), registerDTO.getSmsCode(), SmsTypeEnum.REGISTER);
        if (!isCodeValid) throw new BusinessException("短信验证码错误或已过期");

        CustomerUserEntity exist = customerUserMapper.findByPhone(registerDTO.getPhone());
        if (exist != null) throw new BusinessException("该手机号已注册");

        CustomerUserEntity member = new CustomerUserEntity();
        BeanUtil.copyProperties(registerDTO, member);
        member.setNickName(buildPhoneNickname(registerDTO.getPhone()));
        member.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        member.setCreateTime(new Date());
        member.setUpdateTime(new Date());
        member.setLastLoginTime(new Date());
        member.setValidStatus(true);
        member.setLoginCount(1);
        member.setRegisterSource("phone");
        customerUserMapper.insert(member);
        return buildMemberDTOWithToken(member);
    }

    public MemberDTO login(MemberLoginDTO loginDTO) {
        CustomerUserEntity member = customerUserMapper.findByPhone(loginDTO.getPhone());
        if (member == null) throw new BusinessException("该手机号未注册");
        if (!Boolean.TRUE.equals(member.getValidStatus())) throw new BusinessException("账号已被禁用");
        if (!passwordEncoder.matches(loginDTO.getPassword(), member.getPassword())) throw new BusinessException("密码错误");
        member.setLoginCount(member.getLoginCount() != null ? member.getLoginCount() + 1 : 1);
        member.setLastLoginTime(new Date());
        customerUserMapper.update(member);
        return buildMemberDTOWithToken(member);
    }

    public MemberDTO loginByPhone(MemberPhoneLoginDTO phoneLoginDTO) {
        boolean isCodeValid = verifySmsCode(phoneLoginDTO.getPhone(), phoneLoginDTO.getSmsCode(), SmsTypeEnum.LOGIN);
        if (!isCodeValid) throw new BusinessException("短信验证码错误或已过期");
        CustomerUserEntity member = customerUserMapper.findByPhone(phoneLoginDTO.getPhone());
        if (member == null) throw new BusinessException("该手机号未注册");
        if (!Boolean.TRUE.equals(member.getValidStatus())) throw new BusinessException("账号已被禁用");
        member.setLoginCount(member.getLoginCount() != null ? member.getLoginCount() + 1 : 1);
        member.setLastLoginTime(new Date());
        customerUserMapper.update(member);
        return buildMemberDTOWithToken(member);
    }

    public void logout(String authorization) {
        // C 端：前端清除本地 token 即可，不写入 Redis 黑名单
        // （保留原 blacklist 逻辑参考）
        /*
        if (StringUtils.hasLength(authorization)) {
            String token = TokenUtil.getTokenFromAuthorization(authorization);
            try {
                Claims claims = TokenUtil.parseClaimsFromToken(token, userTokenHelper.getTokenSecret());
                if (claims != null) {
                    String jti = claims.getId();
                    if (StringUtils.hasLength(jti)) {
                        redisUtil.set(BLACKLIST_PREFIX + jti, "1", tokenExpireTimeInRecord);
                    }
                }
            } catch (Exception e) {
                log.warn("登出时解析 token 失败", e);
            }
        }
        */
    }

    public String getCaptchaKey(String uuid) {
        return CAPTCHA_KEY_PREFIX + uuid;
    }

    private boolean verifySmsCode(String phone, String smsCode, SmsTypeEnum smsType) {
        if (!StringUtils.hasLength(smsCode)) return false;
        try {
            SmsRecordConditionDTO condition = new SmsRecordConditionDTO();
            condition.setPhone(phone);
            condition.setSmsTypeEnum(smsType);
            SmsRecordDTO smsRecord = smsRecordFeignClient.findSmsRecord(condition);
            if (smsRecord == null) return false;
            return smsCode.trim().equals(smsRecord.getSmsCode().trim());
        } catch (Exception e) {
            log.warn("校验短信验证码异常", e);
            return false;
        }
    }

    private String buildPhoneNickname(String phone) {
        if (!StringUtils.hasLength(phone)) return "手机号注册用户";
        int len = phone.length();
        if (len >= 7) return phone.substring(0, 3) + "****" + phone.substring(len - 4);
        return "手机号注册用户";
    }

    private MemberDTO buildMemberDTOWithToken(CustomerUserEntity member) {
        String token = TokenUtil.generateToken(
                member.getId(), member.getNickName(),
                Collections.singletonList("ROLE_MEMBER"),
                tokenSecret(), tokenExpireTimeInRecord);
        // C 端：不记录 jti 映射，依赖 JWT 自身过期机制
        // （如果以后需要 C 端 token 主动失效，恢复下面这段）
        /*
        try {
            Claims claims = TokenUtil.parseClaimsFromToken(token, tokenSecret());
            if (claims != null) {
                redisUtil.set(USER_JTI_PREFIX + member.getId(), claims.getId(), tokenExpireTimeInRecord);
            }
        } catch (Exception e) {
            log.warn("存储 jti 映射失败", e);
        }
        */
        MemberDTO dto = new MemberDTO();
        BeanUtil.copyProperties(member, dto);
        dto.setToken(token);
        return dto;
    }

    private String tokenSecret() {
        try {
            return userTokenHelper.getTokenSecret();
        } catch (Exception e) {
            return TOKEN_SECRET_DEFAULT;
        }
    }
}
