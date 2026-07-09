    @Operation(summary = "更新头像",
               description = "内部服务：由 mall-admin-api 通过 Feign 调用，更新用户头像地址")
    @PostMapping("/v1/internal/user/updateAvatar")
    void updateAvatar(@RequestBody @Valid UserAvatarDTO userAvatarDTO);

    /**
     * 更新用户信息
     *
     * @param updateUserDTO
     */
    @Operation(summary = "更新用户信息",
               description = "内部服务：更新用户基本信息，由 mall-admin-api 通过 Feign 调用")
    @PostMapping("/v1/auth/web/user/updateUser")
    void updateUser(@RequestBody @Valid UpdateUserDTO updateUserDTO);

    /**
     * 测试登录（跳过验证码）
     *
     * 仅限开发/测试环境使用，生产环境请走 login() 正常流程
     */
    @Operation(summary = "测试登录",
               description = "内部测试：跳过图形验证码校验，仅限开发/测试环境使用")
    @PostMapping("/v1/internal/user/testLogin")
    TokenDTO testLogin(@RequestBody @Valid UserLoginDTO userLoginDTO);

    /**
     * 获取在线用户列表
     */
    @Operation(summary = "获取在线用户列表",
               description = "查询当前登录状态未过期的管理端用户")
    @GetMapping("/v1/auth/web/user/onlineUsers")
    List<UserDTO> onlineUsers();

    /**
     * 退出登录
     */
    @Operation(summary = "退出登录",
               description = "将当前 token 加入 Redis 黑名单")
    @PostMapping("/v1/auth/web/user/logout")
    void logout();
}