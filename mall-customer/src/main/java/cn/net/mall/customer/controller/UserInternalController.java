package cn.net.mall.customer.controller;

import cn.net.mall.customer.dto.CustomerUserDTO;
import cn.net.mall.customer.service.CustomerUserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * C端用户内部接口（Feign 调用）
 */
@Tag(name = "C端-内部接口", description = "内部Feign调用：C端用户批量查询等")
@RestController
@RequestMapping("/v1/internal/user")
public class UserInternalController {

    private final CustomerUserProfileService customerUserProfileService;

    public UserInternalController(CustomerUserProfileService customerUserProfileService) {
        this.customerUserProfileService = customerUserProfileService;
    }

    @Operation(summary = "批量查询用户信息", description = "内部Feign调用：根据ID集合批量查询C端用户")
    @PostMapping("/findByIds")
    public List<CustomerUserDTO> findByIds(@RequestBody List<Long> ids) {
        return customerUserProfileService.findByIds(ids);
    }
}
