package cn.net.mall.auth.service.auth;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.net.mall.auth.authenication.SmsAuthenticationToken;
import cn.net.mall.auth.dto.*;
import cn.net.mall.auth.entity.auth.*;
import cn.net.mall.auth.entity.auth.web.UserPhoneLoginWebEntity;
import cn.net.mall.auth.entity.auth.web.UserWebEntity;
import cn.net.mall.auth.mapper.auth.*;
import cn.net.mall.auth.util.PasswordUtil;
import cn.net.mall.basic.client.SmsRecordFeignClient;
import cn.net.mall.basic.dto.SmsRecordConditionDTO;
import cn.net.mall.basic.dto.SmsRecordDTO;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.entity.auth.AuthUserEntity;
import cn.net.mall.entity.auth.CaptchaEntity;
import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.entity.auth.TokenEntity;
import cn.net.mall.enums.SmsTypeEnum;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.helper.IdGenerateHelper;
import cn.net.mall.helper.TokenHelper;
import cn.net.mall.helper.UserTokenHelper;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.util.RedisUtil;
import cn.net.mall.util.TokenUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wf.captcha.ArithmeticCaptcha;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static cn.net.mall.auth.util.CaptchaKeyUtil.getCaptchaKey;
import static cn.net.mall.constant.NumberConstant.NUMBER_1;
import static cn.net.mall.constant.NumberConstant.NUMBER_200;
import static cn.net.mall.enums.SmsTypeEnum.BIND_PHONE;
import static cn.net.mall.util.AssertUtil.ASSERT_ERROR_CODE;

/**
 * 用户 服务层
 *
 * @date 2024-01-08 14:03:43
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class UserService extends BaseService<UserEntity, UserConditionEntity> {

    private static final String DEFAULT_PASSWORD = "123456";
    private static final String REGISTER_USER_PREFIX = "registerUser:";

    @Value("${mall.mgt.tokenExpireTimeInRecord:3600}")
    private int tokenExpireTimeInRecord;
    @Value("${mall.mgt.captchaExpireSecond:60}")
    private int captchaExpireSecond;

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final TokenHelper tokenHelper;
    private final PasswordUtil passwordUtil;
    private final RedisUtil redisUtil;
    private final UserDetailsService userDetailsService;
    private final DeptMapper deptMapper;
    private final JobMapper jobMapper;
    private final RoleMapper roleMapper;
    private final IdGenerateHelper idGenerateHelper;
    private final UserAvatarMapper userAvatarMapper;
    private final AuthenticationManager authenticationManager;
    private final SmsRecordFeignClient smsRecordFeignClient;
    private final UserTokenHelper userTokenHelper;

    /**
     * 获取当前登录的用户详情
     *
     * @return 用户详情
     */
    public UserDTO getUserDetail() {
        UserDTO userWebEntity = new UserDTO();
        String currentUsername = tokenHelper.getCurrentUsername();
        UserEntity userEntity = userMapper.findByUserName(currentUsername);
        if (Objects.isNull(userEntity)) {
            return userWebEntity;
        }

        userWebEntity.setId(userEntity.getId());
        userWebEntity.setUserName(userEntity.getUserName());
        userWebEntity.setNickName(userEntity.getNickName());
        userWebEntity.setSex(userEntity.getSex());
        userWebEntity.setPhone(userEntity.getPhone());
        userWebEntity.setBirthday(userEntity.getBirthday());

        UserAvatarEntity userAvatarEntity = userAvatarMapper.findById(userEntity.getAvatarId());
        if (Objects.nonNull(userAvatarEntity)) {
            userWebEntity.setAvatarUrl(userAvatarEntity.getPath());
        }
        return userWebEntity;
    }

    /**
     * 查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    public UserEntity findById(Long id) {
        return userMapper.findById(id);
    }

    /**
     * 通过手机号查询用户信息
     *
     * @param phone 手机号
     * @return 用户信息
     */
    public UserEntity findByIdPhone(String phone) {
        return userMapper.findByPhone(phone);
    }

    /**
     * 根据ID集合批量查询用户
     *
     * @param ids 用户ID集合
     * @return 用户信息
     */
    public List<UserDTO> findByIds(List<Long> ids) {
        UserConditionEntity userConditionEntity = new UserConditionEntity();
        userConditionEntity.setIdList(ids);
        userConditionEntity.setPageNo(0);
        List<UserEntity> productEntities = userMapper.searchByCondition(userConditionEntity);
        return BeanUtil.copyToList(productEntities, UserDTO.class);
    }


    /**
     * 根据条件分页查询用户列表
     *
     * @param userConditionEntity 用户信息
     * @return 用户集合
     */
    public ResponsePageEntity<UserEntity> searchByPage(UserConditionEntity userConditionEntity) {
        ResponsePageEntity<UserEntity> responsePageEntity = super.searchByPage(userConditionEntity);
        List<UserEntity> data = responsePageEntity.getData();
        if (CollectionUtils.isNotEmpty(data)) {
            initDept(responsePageEntity.getData());
            initRole(responsePageEntity.getData());
            initJob(responsePageEntity.getData());
        }
        return responsePageEntity;
    }

    private void initDept(List<UserEntity> userEntities) {
        if (CollectionUtils.isEmpty(userEntities)) {
            return;
        }

        List<Long> deptIdList = userEntities.stream().map(UserEntity::getDeptId).distinct().collect(Collectors.toList());
        List<DeptEntity> deptList = deptMapper.findByIds(deptIdList);
        Map<Long, List<DeptEntity>> deptMap = deptList.stream().collect(Collectors.groupingBy(DeptEntity::getId));
        for (UserEntity userEntity : userEntities) {
            List<DeptEntity> deptEntities = deptMap.get(userEntity.getDeptId());
            if (CollectionUtils.isNotEmpty(deptEntities)) {
                userEntity.setDept(deptEntities.get(0));
            }
        }
    }

    private void initRole(List<UserEntity> userEntities) {
        List<Long> userIdList = userEntities.stream().map(UserEntity::getId).distinct().collect(Collectors.toList());
        UserRoleConditionEntity userRoleConditionEntity = new UserRoleConditionEntity();
        userRoleConditionEntity.setUserIdList(userIdList);
        List<UserRoleEntity> userRoleEntityList = userRoleMapper.searchByCondition(userRoleConditionEntity);
        Map<Long, List<UserRoleEntity>> userRoleMap = userRoleEntityList.stream().collect(Collectors.groupingBy(UserRoleEntity::getUserId));
        for (UserEntity userEntity : userEntities) {
            List<UserRoleEntity> list = userRoleMap.get(userEntity.getId());
            if (CollectionUtils.isNotEmpty(list)) {
                userEntity.setRoles(list.stream().map(x -> {
                    RoleEntity roleEntity = new RoleEntity();
                    roleEntity.setId(x.getRoleId());
                    return roleEntity;
                }).collect(Collectors.toList()));
            }
        }
    }

    private void initJob(List<UserEntity> userEntities) {
        for (UserEntity userEntity : userEntities) {
            if (Objects.isNull(userEntity.getJobId())) {
                continue;
            }
            JobEntity jobEntity = new JobEntity();
            jobEntity.setId(userEntity.getJobId());
            userEntity.setJobs(Lists.newArrayList(jobEntity));
        }
    }

    /**
     * 初始化历史用户数据到Redis
     */
    public void initHistoryUserToRedis() {
        UserConditionEntity userConditionEntity = new UserConditionEntity();
        userConditionEntity.setPageNo(NUMBER_1);
        userConditionEntity.setPageSize(NUMBER_200);

        List<UserEntity> userList = userMapper.searchByCondition(userConditionEntity);

        while (CollectionUtils.isNotEmpty(userList)) {
            saveUserToRedis(userList);

            userConditionEntity.setPageNo(userConditionEntity.getPageNo() + 1);
            userList = userMapper.searchByCondition(userConditionEntity);
        }
    }

    private void saveUserToRedis(List<UserEntity> userList) {
        for (UserEntity userEntity : userList) {
            saveUserToRedis(userEntity);
        }
    }

    private void fillData(List<UserEntity> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        List<Long> deptIdList = dataList.stream()
                .filter(x -> Objects.nonNull(x.getDeptId()))
                .map(UserEntity::getDeptId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(deptIdList)) {
            return;
        }
        DeptConditionEntity deptConditionEntity = new DeptConditionEntity();
        deptConditionEntity.setIdList(deptIdList);
        List<DeptEntity> deptEntities = deptMapper.searchByCondition(deptConditionEntity);
        Map<Long, List<DeptEntity>> deptMap = deptEntities.stream().collect(Collectors.groupingBy(DeptEntity::getId));
        for (UserEntity userEntity : dataList) {
            if (Objects.isNull(userEntity.getDeptId())) {
                continue;
            }

            List<DeptEntity> deptEntityList = deptMap.get(userEntity.getDeptId());
            if (CollectionUtils.isNotEmpty(deptEntityList)) {
                DeptEntity deptEntity = deptEntityList.get(0);
                userEntity.setDeptName(deptEntity.getName());
                userEntity.setDept(deptEntity);
            }
        }
        fillDept(dataList);
        fillRole(dataList);
    }


    private void fillDept(List<UserEntity> dataList) {
        List<Long> jobIdList = dataList.stream().filter(x -> Objects.nonNull(x.getJobId())).map(UserEntity::getJobId).collect(Collectors.toList());
        List<JobEntity> deptList = jobMapper.findByIds(jobIdList);
        for (UserEntity userEntity : dataList) {
            Optional<JobEntity> optional = deptList.stream().filter(x -> x.getId().equals(userEntity.getJobId())).findAny();
            if (optional.isPresent()) {
                userEntity.setJobs(Lists.newArrayList(optional.get()));
            }
        }
    }


    private void fillRole(List<UserEntity> dataList) {
        List<Long> userIdList = dataList.stream().map(UserEntity::getId).collect(Collectors.toList());
        UserRoleConditionEntity userRoleConditionEntity = new UserRoleConditionEntity();
        userRoleConditionEntity.setUserIdList(userIdList);
        userRoleConditionEntity.setPageNo(0);
        List<UserRoleEntity> userRoleEntityList = userRoleMapper.searchByCondition(userRoleConditionEntity);
        if (CollectionUtils.isEmpty(userRoleEntityList)) {
            return;
        }

        List<Long> roleIdList = userRoleEntityList.stream().map(UserRoleEntity::getRoleId).distinct().collect(Collectors.toList());
        List<RoleEntity> roleList = roleMapper.findByIds(roleIdList);
        Map<Long, List<UserRoleEntity>> userRoleMap = userRoleEntityList.stream().collect(Collectors.groupingBy(UserRoleEntity::getUserId));
        Map<Long, List<RoleEntity>> roleMap = roleList.stream().collect(Collectors.groupingBy(RoleEntity::getId));
        for (UserEntity userEntity : dataList) {
            List<UserRoleEntity> roleEntities = userRoleMap.get(userEntity.getId());
            if (CollectionUtils.isNotEmpty(roleEntities)) {
                List<RoleEntity> roles = Lists.newArrayList();
                for (UserRoleEntity roleEntity : roleEntities) {
                    List<RoleEntity> matchRoleEntities = roleMap.get(roleEntity.getRoleId());
                    if (CollectionUtils.isNotEmpty(matchRoleEntities)) {
                        roles.add(matchRoleEntities.get(0));
                    }
                }
                userEntity.setRoles(roles);
            }
        }
    }

    /**
     * 用户手机号登录
     *
     * @param userPhoneLoginDTO 用户实体
     * @return 影响行数
     */
    public TokenDTO loginByPhone(UserPhoneLoginDTO userPhoneLoginDTO) {
        try {
            SmsAuthenticationToken authenticationToken =
                    new SmsAuthenticationToken(userPhoneLoginDTO.getPhone(), userPhoneLoginDTO.getSmsCode());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            JwtUserEntity jwtUserEntity = (JwtUserEntity) (authentication.getPrincipal());
            UserEntity userEntity = userMapper.findByPhone(userPhoneLoginDTO.getPhone());
            AssertUtil.notNull(userEntity, "该用户不存在");
            AssertUtil.isTrue(userEntity.getValidStatus() == null || userEntity.getIsDel() == 0, "该账号已注销，无法登录");
            AssertUtil.isTrue(Boolean.TRUE.equals(userEntity.getValidStatus()), "该账号已注销，无法登录");

            String token = tokenHelper.generateToken(jwtUserEntity);
            List<String> roles = jwtUserEntity.getAuthorities().stream()
                    .map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList());
            return new TokenDTO(jwtUserEntity.getUsername(), token, roles, tokenExpireTimeInRecord);
        } catch (Exception e) {
            log.info("登录失败：", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("用户名登录失败");
        }
    }

    /**
     * 用户登录
     *
     * @param userLoginDTO 用户录入信息
     */
    public TokenDTO login(UserLoginDTO userLoginDTO) {
        String code = redisUtil.get(getCaptchaKey(userLoginDTO.getUuid()));
        AssertUtil.hasLength(code, "该验证码已失效");
        AssertUtil.isTrue(code.trim().equals(userLoginDTO.getCode().trim()), "验证码错误");

        try {
            String decodePassword = passwordUtil.decodeRsaPassword(userLoginDTO.getPassword());
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), decodePassword);
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            JwtUserEntity jwtUserEntity = (JwtUserEntity) (authentication.getPrincipal());
            UserEntity userEntity = userMapper.findByUserName(jwtUserEntity.getUsername());
            AssertUtil.notNull(userEntity, "该用户不存在");
            AssertUtil.isTrue(userEntity.getIsDel() == null || userEntity.getIsDel() == 0, "该账号已注销，无法登录");
            AssertUtil.isTrue(Boolean.TRUE.equals(userEntity.getValidStatus()), "该账号已注销，无法登录");

            String token = tokenHelper.generateToken(jwtUserEntity);
            redisUtil.del(getCaptchaKey(userLoginDTO.getUuid()));
            List<String> roles = jwtUserEntity.getAuthorities().stream()
                    .map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList());

            //todo 记录最后一次登录时间和地点
            return new TokenDTO(jwtUserEntity.getUsername(), token, roles, tokenExpireTimeInRecord);
        } catch (Exception e) {
            log.info("登录失败：", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException(ASSERT_ERROR_CODE, "用户名或密码错误");
        }
    }


    /**
     * 用户登出
     *
     * @param request 请求
     */
    public void logout(HttpServletRequest request) {
        String token = TokenUtil.getTokenForAuthorization(request);
        AssertUtil.hasLength(token, "请重新登录");
        tokenHelper.delToken(token);
    }

    public void cancelAccount(HttpServletRequest request) {
        String token = TokenUtil.getTokenForAuthorization(request);
        AssertUtil.hasLength(token, "请重新登录");

        JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfo();
        UserEntity userEntity = userMapper.findById(currentUserInfo.getId());
        AssertUtil.notNull(userEntity, "该用户不存在");

        UserEntity updateEntity = new UserEntity();
        updateEntity.setId(userEntity.getId());
        updateEntity.setValidStatus(false);
        updateEntity.setIsDel(1);
        FillUserUtil.fillUpdateUserInfo(updateEntity);
        userMapper.update(updateEntity);

        tokenHelper.delToken(token);
    }


    /**
     * 获取当前登录的用户信息
     *
     * @return 用户信息
     */
    public JwtUserEntity getUserInfo() {
        String currentUsername = tokenHelper.getCurrentUsername();
        return (JwtUserEntity) userDetailsService.loadUserByUsername(currentUsername);
    }

    /**
     * 更新用户信息
     *
     * @param updateUserDTO 用户实体
     * @return 影响行数
     */
    public boolean updateUser(UpdateUserDTO updateUserDTO) {
        UserEntity userEntity = userMapper.findById(updateUserDTO.getId());
        AssertUtil.notNull(userEntity, "该用户不存在");

        userEntity.setNickName(updateUserDTO.getNickName());
        userEntity.setSex(updateUserDTO.getSex());
        userEntity.setBirthday(updateUserDTO.getBirthday());
        return userMapper.update(userEntity) > 0;
    }

    /**
     * 重置密码
     *
     * @param resetPasswordDTO 参数
     * @return
     */
    public boolean resetPassword(ResetPasswordDTO resetPasswordDTO) {
        // 先验证手机号和验证码
        String code = redisUtil.get(getCaptchaKey(resetPasswordDTO.getUuid()));
        AssertUtil.hasLength(code, "该验证码已失效");
        AssertUtil.isTrue(code.trim().equals(resetPasswordDTO.getVerifyCode().trim()), "验证码错误");

        // 短信验证验证码
        boolean isCodeValid = verifySmsCode(resetPasswordDTO.getPhone(), resetPasswordDTO.getSmsCode(), SmsTypeEnum.RESET_PASSWORD);
        if (!isCodeValid) {
            throw new BusinessException("短信验证码错误或已过期");
        }

        // 查询用户
        UserEntity user = userMapper.findByPhone(resetPasswordDTO.getPhone());
        if (user == null) {
            throw new BusinessException("该手机号在系统中不存在");
        }

        // 更新密码
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setPassword(passwordUtil.encode(resetPasswordDTO.getPassword()));
        userEntity.setUpdateTime(new Date());
        userEntity.setUpdateTime(new Date());
        userEntity.setUpdateUserId(user.getCreateUserId());
        userEntity.setUpdateUserName(user.getCreateUserName());

        // 更新用户信息
        userMapper.update(userEntity);

        return true;

    }

    /**
     * 绑定手机号
     *
     * @param bindPhoneDTO 绑定手机号实体
     */
    public void bindPhone(BindPhoneDTO bindPhoneDTO) {
        String phone = bindPhoneDTO.getNewPhone();
        String verifyCode = bindPhoneDTO.getSmsCode();

        // 获取当前用户
        Long userId = FillUserUtil.getCurrentUserInfo().getId();
        UserEntity user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证新手机号不能与当前手机号相同
        if (phone.equals(user.getPhone())) {
            throw new BusinessException("新手机号不能与当前手机号相同");
        }

        // 验证手机号是否已被使用
        UserEntity existUser = userMapper.findByPhone(phone);
        if (existUser != null) {
            throw new BusinessException("该手机号已被使用");
        }

        // 验证验证码
        boolean isCodeValid = verifySmsCode(phone, verifyCode, BIND_PHONE);
        if (!isCodeValid) {
            throw new BusinessException("短信验证码错误或已过期");
        }

        // 更新用户手机号
        user.setPhone(phone);
        userMapper.update(user);
    }

    /**
     * 用户注册
     *
     * @param registerDTO 用户实体
     * @return 注册成功后的用户信息
     */
    public UserDTO register(RegisterDTO registerDTO) {
        try {
            //校验动态验证码
            String code = redisUtil.get(getCaptchaKey(registerDTO.getUuid()));
            AssertUtil.hasLength(code, "该验证码已失效");
            AssertUtil.isTrue(code.trim().equals(registerDTO.getCode().trim()), "验证码错误");

            // 短信验证验证码
            boolean isCodeValid = verifySmsCode(registerDTO.getPhone(), registerDTO.getSmsCode(), SmsTypeEnum.REGISTER);
            if (!isCodeValid) {
                throw new BusinessException("短信验证码错误或已过期");
            }

            // 注册用户
            UserDTO userDTO = doRegister(registerDTO);

            // 生成token
            String token = userTokenHelper.generateToken(userDTO.getUserName(), JSONUtil.toJsonStr(userDTO));
            userDTO.setToken(token);

            return userDTO;
        } catch (Exception e) {
            log.info("注册用户失败，原因：", e);
            if (e instanceof BusinessException) {
                throw e;
            } else {
                throw new BusinessException("系统错误");
            }

        }
    }


    public boolean verifySmsCode(String phone, String code, SmsTypeEnum type) {
        SmsRecordConditionDTO smsRecordConditionDTO = new SmsRecordConditionDTO();
        smsRecordConditionDTO.setPhone(phone);
        smsRecordConditionDTO.setSmsTypeEnum(type);
        SmsRecordDTO smsRecord = smsRecordFeignClient.findSmsRecord(smsRecordConditionDTO);

        if (smsRecord == null) {
            return false;
        }

        // 验证码是否过期
        long betweenSecond = DateUtil.between(new Date(), smsRecord.getSendTime(), DateUnit.SECOND);
        if (smsRecord.getExpireSecond() < betweenSecond) {
            return false;
        }

        // 验证码是否匹配
        return smsRecord.getSmsCode().equals(code);
    }

    /**
     * 用户注册
     *
     * @param registerDTO 注册实体
     * @return 注册成功后的用户信息
     */
    private UserDTO doRegister(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        UserEntity existUser = userMapper.findByUserName(registerDTO.getUserName());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已注册
        existUser = userMapper.findByPhone(registerDTO.getPhone());
        if (existUser != null) {
            throw new BusinessException("该手机号已注册");
        }

        // 创建用户
        UserEntity user = new UserEntity();
        BeanUtils.copyProperties(registerDTO, user);
        user.setNickName(buildPhoneNickname(registerDTO.getPhone()));

        // 密码加密
        user.setPassword(passwordUtil.encode(registerDTO.getPassword()));
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setLastLoginTime(new Date());
        user.setValidStatus(true); // 1-正常
        user.setCreateUserId(1L);
        user.setCreateUserName("系统管理员");
        // 保存用户
        userMapper.insert(user);

        // 返回用户信息
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);

        return userDTO;
    }

    private String buildPhoneNickname(String phone) {
        if (!org.springframework.util.StringUtils.hasLength(phone)) {
            return "手机号注册用户";
        }
        int len = phone.length();
        if (len <= 7) {
            return phone;
        }
        String prefix = phone.substring(0, 3);
        String suffix = phone.substring(len - 4);
        return prefix + "..." + suffix;
    }

    public CaptchaEntity getCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(111, 36);
        // 几位数运算，默认是两位
        captcha.setLen(2);
        // 获取运算的结果
        String result = "";
        try {
            result = new BigDecimal(captcha.text()).intValue() + "";
        } catch (Exception e) {
            result = captcha.text();
        }
        String uuid = "C" + IdUtil.simpleUUID();
        // 保存验证码到Redis中
        redisUtil.set(getCaptchaKey(uuid), result, captchaExpireSecond);
        return new CaptchaEntity(uuid, captcha.toBase64());
    }


    /**
     * 新增用户
     *
     * @param userEntity 用户信息
     * @return 结果
     */
    @Transactional(rollbackFor = Throwable.class)
    public void insert(UserEntity userEntity) {
        UserConditionEntity userConditionEntity = new UserConditionEntity();
        userConditionEntity.setUserName(userEntity.getUserName());
        AssertUtil.isTrue(CollectionUtils.isEmpty(userMapper.searchByCondition(userConditionEntity)), "用户名称已存在");

        userConditionEntity = new UserConditionEntity();
        userConditionEntity.setEmail(userEntity.getEmail());
        AssertUtil.isTrue(CollectionUtils.isEmpty(userMapper.searchByCondition(userConditionEntity)), "邮箱已存在");
        if (!StringUtils.hasLength(userEntity.getPassword())) {
            userEntity.setPassword(DEFAULT_PASSWORD);
        }
        userEntity.setPassword(passwordUtil.encode(userEntity.getPassword()));
        fillData(userEntity);

        userMapper.insert(userEntity);

        userRoleMapper.deleteByUserId(userEntity.getId());
        List<UserRoleEntity> userRoleEntities = buildUserRoleEntityList(userEntity);
        if (CollectionUtils.isNotEmpty(userRoleEntities)) {
            userRoleMapper.batchInsert(userRoleEntities);
        }
        //秒杀系统会用到
        saveUserToRedis(userEntity);
    }

    private void saveUserToRedis(UserEntity userEntity) {
        redisUtil.setIfAbsent(getUserKey(userEntity.getUserName()), JSON.toJSONString(userEntity));
    }

    private String getUserKey(String userName) {
        return String.format("%s%s", REGISTER_USER_PREFIX, userName);
    }

    private void fillData(UserEntity userEntity) {
        if (Objects.nonNull(userEntity.getDept())) {
            userEntity.setDeptId(userEntity.getDept().getId());
        }

        if (CollectionUtils.isNotEmpty(userEntity.getJobs())) {
            userEntity.setJobId(userEntity.getJobs().get(0).getId());
        }
    }

    private List<UserRoleEntity> buildUserRoleEntityList(UserEntity userEntity) {
        if (CollectionUtils.isNotEmpty(userEntity.getRoles())) {
            return userEntity.getRoles().stream().map(x -> {
                UserRoleEntity userRoleEntity = new UserRoleEntity();
                userRoleEntity.setId(idGenerateHelper.nextId());
                userRoleEntity.setUserId(userEntity.getId());
                userRoleEntity.setRoleId(x.getId());
                return userRoleEntity;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 修改用户
     *
     * @param userEntity 用户信息
     * @return 结果
     */
    @Transactional(rollbackFor = Throwable.class)
    public int update(UserEntity userEntity) {
        fillData(userEntity);
        userRoleMapper.deleteByUserId(userEntity.getId());
        List<UserRoleEntity> userRoleEntities = buildUserRoleEntityList(userEntity);
        if (CollectionUtils.isNotEmpty(userRoleEntities)) {
            userRoleMapper.batchInsert(userRoleEntities);
        }
        return userMapper.update(userEntity);
    }


    /**
     * 删除岗位对象
     *
     * @param ids 系统ID
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<UserEntity> userEntities = userMapper.findByIds(ids);
        AssertUtil.notEmpty(userEntities, "用户已被删除");

        UserEntity userEntity = new UserEntity();
        FillUserUtil.fillUpdateUserInfo(userEntity);
        return userMapper.deleteByIds(ids, userEntity);
    }

    /**
     * 更新用户头像
     *
     * @param userAvatarDTO 用户头像实体
     * @return 用户头像地址
     */
    @Transactional(rollbackFor = Throwable.class)
    public void updateAvatar(UserAvatarDTO userAvatarDTO) {
        try {
            String fileUrl = userAvatarDTO.getFileUrl();
            // 更新用户头像
            UserEntity userEntity = userMapper.findById(FillUserUtil.getCurrentUserInfo().getId());
            if (userEntity == null) {
                throw new BusinessException("用户不存在");
            }

            UserAvatarEntity userAvatarEntity = new UserAvatarEntity();
            userAvatarEntity.setFileName(userAvatarDTO.getFileName());
            userAvatarEntity.setPath(fileUrl);
            userAvatarEntity.setCreateUserId(userEntity.getId());
            userAvatarEntity.setCreateUserName(userEntity.getUserName());
            userAvatarEntity.setCreateTime(new Date());
            userAvatarEntity.setId(idGenerateHelper.nextId());
            userAvatarEntity.setIsDel(0);
            userAvatarMapper.insert(userAvatarEntity);

            userEntity.setAvatarId(userAvatarEntity.getId());
            userEntity.setUpdateTime(new Date());
            userMapper.update(userEntity);
        } catch (Exception e) {
            log.error("上传头像失败", e);
            throw new BusinessException("上传头像失败: " + e.getMessage());
        }
    }

    /**
     * 批量重置用户密码
     *
     * @param ids 用户ID
     * @return
     */
    public int resetPwd(List<Long> ids) {
        List<UserEntity> userEntities = userMapper.findByIds(ids);
        AssertUtil.notEmpty(userEntities, "用户不存在");

        for (UserEntity userEntity : userEntities) {
            userEntity.setPassword(passwordUtil.encode(DEFAULT_PASSWORD));
            FillUserUtil.fillUpdateUserInfo(userEntity);
        }
        return userMapper.updateForBatch(userEntities);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return userMapper;
    }
}
