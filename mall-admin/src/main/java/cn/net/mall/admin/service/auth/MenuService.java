package cn.net.mall.admin.service.auth;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.admin.dto.auth.MenuTreeDTO;
import cn.net.mall.admin.dto.auth.MetaDTO;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.admin.entity.auth.MenuConditionEntity;
import cn.net.mall.admin.entity.auth.MenuEntity;
import cn.net.mall.admin.entity.auth.RoleEntity;
import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.redis.TokenHelper;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.admin.mapper.auth.MenuMapper;
import cn.net.mall.admin.mapper.auth.RoleMenuMapper;
import cn.net.mall.admin.mapper.auth.RoleMapper;
import cn.net.mall.admin.mapper.auth.UserRoleMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 菜单 服务层
 *
 * @date 2024-01-08 14:03:44
 */
@Service
public class MenuService extends BaseService<MenuEntity, MenuConditionEntity> {

    private final MenuMapper menuMapper;
    private final TokenHelper tokenHelper;
    private final RoleMenuMapper roleMenuMapper;
    private final RoleMapper roleMapper;

    public MenuService(MenuMapper menuMapper, TokenHelper tokenHelper,
                       RoleMenuMapper roleMenuMapper, RoleMapper roleMapper) {
        this.menuMapper = menuMapper;
        this.tokenHelper = tokenHelper;
        this.roleMenuMapper = roleMenuMapper;
        this.roleMapper = roleMapper;
    }

    /**
     * 查询菜单信息
     *
     * @param id 菜单ID
     * @return 菜单信息
     */
    public MenuEntity findById(Long id) {
        return menuMapper.findById(id);
    }

    /**
     * 根据条件分页查询菜单列表
     *
     * @param menuConditionEntity 菜单信息
     * @return 菜单集合
     */
    public ResponsePageEntity<MenuEntity> searchByPage(MenuConditionEntity menuConditionEntity) {
        return super.searchByPage(menuConditionEntity);
    }

    /**
     * 获取下级菜单
     *
     * @param id 菜单ID
     * @return 下级菜单
     */
    public List<Long> getChild(Long id) {
        List<Long> result = Lists.newArrayList(id);
        MenuConditionEntity menuConditionEntity = new MenuConditionEntity();
        menuConditionEntity.setPageNo(0);
        menuConditionEntity.setPid(id);
        List<MenuEntity> menuEntities = menuMapper.searchByCondition(menuConditionEntity);
        List<Long> childIds = menuEntities.stream().map(MenuEntity::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(childIds)) {
            result.addAll(childIds);
        }
        return result;
    }

    /**
     * 获取菜单树
     *
     * @return 菜单树
     */
    public List<MenuTreeDTO> getMenuTree() {
        MenuConditionEntity menuConditionEntity = new MenuConditionEntity();
        menuConditionEntity.setPageNo(0);
        menuConditionEntity.setPid(0L);
        menuConditionEntity.setSortField(Lists.newArrayList("sort,desc"));
        List<MenuEntity> menuEntities = menuMapper.searchByCondition(menuConditionEntity);
        if (CollectionUtils.isEmpty(menuEntities)) {
            return Collections.emptyList();
        }

        List<MenuTreeDTO> result = Lists.newArrayList();
        for (MenuEntity menuEntity : menuEntities) {
            MenuTreeDTO menuTreeDTO = buildMenuTreeDTO(menuEntity);
            menuTreeDTO.setAlwaysShow(true);
            result.add(menuTreeDTO);
            buildChildren(menuEntity, menuTreeDTO);
        }
        return result;
    }

    /**
     * 获取当前登录用户的菜单树
     *
     * <p>根据当前用户拥有的角色，过滤出有权限的菜单树（不含按钮）。
     * 前端登录后调此接口渲染侧边栏。</p>
     *
     * @return 当前用户可见的菜单树
     */
    public List<MenuTreeDTO> getCurrentUserMenuTree() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserEntity jwtUser)) {
            return Collections.emptyList();
        }

        // 查用户拥有的角色
        List<RoleEntity> roles = roleMapper.findRoleByUserId(jwtUser.getId());
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        List<Long> roleIds = roles.stream().map(RoleEntity::getId).collect(Collectors.toList());

        // 查角色关联的菜单
        List<MenuEntity> menuList = menuMapper.findMenuByRoleIdList(roleIds);
        if (CollectionUtils.isEmpty(menuList)) {
            return Collections.emptyList();
        }

        // 构建菜单树（只保留目录和菜单，不含按钮）
        return buildMenuTreeFromList(menuList);
    }

    /**
     * 从菜单实体列表构建菜单树
     *
     * @param menuList 扁平菜单列表
     * @return 树形菜单结构
     */
    private List<MenuTreeDTO> buildMenuTreeFromList(List<MenuEntity> menuList) {
        // 按 pid 分组
        Map<Long, List<MenuEntity>> menuMap = menuList.stream()
                .collect(Collectors.groupingBy(MenuEntity::getPid));

        // 找顶级菜单（pid = 0）
        List<MenuEntity> topMenus = menuMap.getOrDefault(0L, Collections.emptyList());
        List<MenuTreeDTO> result = Lists.newArrayList();
        for (MenuEntity menu : topMenus) {
            result.add(buildTreeNode(menu, menuMap));
        }
        return result;
    }

    private MenuTreeDTO buildTreeNode(MenuEntity menu, Map<Long, List<MenuEntity>> menuMap) {
        MenuTreeDTO node = buildMenuTreeDTO(menu);
        List<MenuEntity> children = menuMap.get(menu.getId());
        if (CollectionUtils.isNotEmpty(children)) {
            node.setAlwaysShow(true);
            for (MenuEntity child : children) {
                node.addChildren(buildTreeNode(child, menuMap));
            }
        }
        return node;
    }

    public List<MenuTreeDTO> getMenu(MenuConditionEntity menuConditionEntity) {
        List<MenuTreeDTO> result = Lists.newArrayList();
        if (Objects.isNull(menuConditionEntity.getPid())) {
            menuConditionEntity.setPid(0L);
        }
        menuConditionEntity.setPageNo(0);
        List<MenuEntity> menuEntities = menuMapper.searchByCondition(menuConditionEntity);
        List<Long> pidList = menuEntities.stream().map(MenuEntity::getId).collect(Collectors.toList());

        MenuConditionEntity childrenMenuConditionEntity = new MenuConditionEntity();
        childrenMenuConditionEntity.setPidList(pidList);
        List<MenuEntity> childrenEntities = menuMapper.searchByCondition(childrenMenuConditionEntity);
        Map<Long, List<MenuEntity>> childrenMenuMap = childrenEntities.stream().collect(Collectors.groupingBy(MenuEntity::getPid));
        for (MenuEntity menuEntity : menuEntities) {
            MenuTreeDTO childrenMenuTreeDTO = buildMenuTreeDTO(menuEntity);
            List<MenuEntity> subMenuEntities = childrenMenuMap.get(menuEntity.getId());
            if (CollectionUtils.isNotEmpty(subMenuEntities)) {
                childrenMenuTreeDTO.setLeaf(false);
                childrenMenuTreeDTO.setSubCount(subMenuEntities.size());
            } else {
                childrenMenuTreeDTO.setLeaf(true);
                childrenMenuTreeDTO.setSubCount(0);
            }
            childrenMenuTreeDTO.setHasChildren(!childrenMenuTreeDTO.getLeaf());
            result.add(childrenMenuTreeDTO);
        }
        return result;
    }


    private void buildChildren(MenuEntity menuEntity, MenuTreeDTO menuTreeDTO) {
        MenuConditionEntity menuConditionEntity = new MenuConditionEntity();
        menuConditionEntity.setPageNo(0);
        menuConditionEntity.setPid(menuEntity.getId());
        menuConditionEntity.setSortField(Lists.newArrayList("sort,desc"));
        List<MenuEntity> childrenEntities = menuMapper.searchByCondition(menuConditionEntity);
        if (CollectionUtils.isNotEmpty(childrenEntities)) {
            for (MenuEntity childrenEntity : childrenEntities) {
                MenuTreeDTO childrenMenuTreeDTO = buildMenuTreeDTO(childrenEntity);
                menuTreeDTO.addChildren(childrenMenuTreeDTO);
                buildChildren(childrenEntity, childrenMenuTreeDTO);
            }
        }
    }


    private MenuTreeDTO buildMenuTreeDTO(MenuEntity menuEntity) {
        MenuTreeDTO menuTreeDTO = BeanUtil.copyProperties(menuEntity, MenuTreeDTO.class);
        menuTreeDTO.setLabel(menuEntity.getName());
        menuTreeDTO.setAlwaysShow(false);
        MetaDTO metaDTO = new MetaDTO();
        menuTreeDTO.setMeta(metaDTO);
        metaDTO.setIcon(menuTreeDTO.getIcon());
        metaDTO.setTitle(menuTreeDTO.getLabel());
        metaDTO.setNoCache(true);
        return menuTreeDTO;
    }


    /**
     * 保存菜单
     *
     * @param menuEntity 菜单信息
     */
    public void save(MenuEntity menuEntity) {
        if (Objects.isNull(menuEntity.getId())) {
            FillUserUtil.fillCreateUserInfo(menuEntity);
            menuMapper.insert(menuEntity);
        } else {
            FillUserUtil.fillUpdateUserInfo(menuEntity);
            menuMapper.update(menuEntity);
        }
    }

    /**
     * 新增菜单
     *
     * @param menuEntity 菜单信息
     * @return 结果
     */
    public int insert(MenuEntity menuEntity) {
        FillUserUtil.fillCreateUserInfo(menuEntity);
        return menuMapper.insert(menuEntity);
    }

    /**
     * 修改菜单
     *
     * @param menuEntity 菜单信息
     * @return 结果
     */
    public int update(MenuEntity menuEntity) {
        AssertUtil.notNull(menuEntity.getId(), "菜单ID不能为空");
        FillUserUtil.fillUpdateUserInfo(menuEntity);
        return menuMapper.update(menuEntity);
    }

    /**
     * 删除菜单对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<MenuEntity> menuEntities = menuMapper.findByIds(ids);
        AssertUtil.notEmpty(menuEntities, "菜单已被删除");

        MenuEntity menuEntity = new MenuEntity();
        FillUserUtil.fillUpdateUserInfo(menuEntity);
        return menuMapper.deleteByIds(ids, menuEntity);

    }


    @Override
    protected BaseMapper getBaseMapper() {
        return menuMapper;
    }
}
