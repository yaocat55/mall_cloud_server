package cn.net.mall.customer.service;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.customer.dto.CustomerAvatarDTO;
import cn.net.mall.customer.dto.CustomerUpdateDTO;
import cn.net.mall.customer.dto.CustomerUserDTO;
import cn.net.mall.customer.entity.CustomerUserEntity;
import cn.net.mall.customer.mapper.CustomerUserMapper;
import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.util.FillUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * C端用户信息管理服务
 */
@Slf4j
@Service
public class CustomerUserProfileService {

    private final CustomerUserMapper customerUserMapper;

    public CustomerUserProfileService(CustomerUserMapper customerUserMapper) {
        this.customerUserMapper = customerUserMapper;
    }

    private Long getCurrentCustomerId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserEntity jwtUser) {
            return jwtUser.getId();
        }
        return null;
    }

    public CustomerUserDTO getUserDetail() {
        Long id = getCurrentCustomerId();
        if (id == null) return null;
        CustomerUserEntity entity = customerUserMapper.findById(id);
        if (entity == null) return null;
        return BeanUtil.copyProperties(entity, CustomerUserDTO.class);
    }

    public void updateUser(CustomerUpdateDTO dto) {
        CustomerUserEntity entity = customerUserMapper.findById(dto.getId());
        if (entity == null) return;
        BeanUtil.copyProperties(dto, entity);
        FillUserUtil.fillUpdateUserInfo(entity);
        customerUserMapper.update(entity);
    }

    public void updateAvatar(CustomerAvatarDTO dto) {
        CustomerUserEntity entity = customerUserMapper.findById(dto.getUserId());
        if (entity == null) return;
        entity.setAvatarUrl(dto.getFileUrl());
        FillUserUtil.fillUpdateUserInfo(entity);
        customerUserMapper.update(entity);
    }

    public List<CustomerUserDTO> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        List<CustomerUserEntity> entities = customerUserMapper.findByIds(ids);
        return entities.stream()
                .map(e -> BeanUtil.copyProperties(e, CustomerUserDTO.class))
                .collect(Collectors.toList());
    }
}
