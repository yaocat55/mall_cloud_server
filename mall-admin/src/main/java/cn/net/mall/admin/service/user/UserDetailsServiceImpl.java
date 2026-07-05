package cn.net.mall.admin.service.user;

import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.admin.entity.auth.MenuEntity;
import cn.net.mall.admin.entity.auth.RoleEntity;
import cn.net.mall.admin.entity.auth.UserEntity;
import cn.net.mall.admin.mapper.auth.MenuMapper;
import cn.net.mall.admin.mapper.auth.RoleMapper;
import cn.net.mall.admin.mapper.auth.UserMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @date 2024/1/12 下午6:08
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private MenuMapper menuMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userMapper.findByUserName(username);
        if (Objects.isNull(userEntity)) {
            return null;
        }
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        fillUserAuthority(userEntity, authorities);
        List<String> roles = authorities.stream()
                .map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList());
        JwtUserEntity jwtUserEntity = new JwtUserEntity(userEntity.getId(),
                username,
                userEntity.getPassword(),
                authorities,
                roles);
        return jwtUserEntity;
    }

    private void fillUserAuthority(UserEntity userEntity, List<SimpleGrantedAuthority> authorities) {
        List<RoleEntity> roleEntities = roleMapper.findRoleByUserId(userEntity.getId());
        if (CollectionUtils.isEmpty(roleEntities)) {
            return;
        }

        Set<String> permissionSet = roleEntities.stream()
                .filter(x -> StringUtils.hasLength(x.getPermission())).map(RoleEntity::getPermission)
                .collect(Collectors.toSet());
        fillRoleMenu(roleEntities, permissionSet);
        if (CollectionUtils.isNotEmpty(permissionSet)) {
            authorities.addAll(permissionSet.stream().map(x -> new SimpleGrantedAuthority(x)).collect(Collectors.toList()));
        }
    }

    private void fillRoleMenu(List<RoleEntity> roleEntities, Set<String> permissionSet) {
        List<Long> roleIdList = roleEntities.stream().map(RoleEntity::getId).collect(Collectors.toList());
        List<MenuEntity> menuList = menuMapper.findMenuByRoleIdList(roleIdList);
        if (CollectionUtils.isEmpty(menuList)) {
            return;
        }

        for (MenuEntity menuEntity : menuList) {
            if (StringUtils.hasLength(menuEntity.getPermission())) {
                Set<String> menuPermissionSet = Arrays.stream(menuEntity.getPermission().split(",")).collect(Collectors.toSet());
                permissionSet.addAll(menuPermissionSet);
            }
        }
    }
}
