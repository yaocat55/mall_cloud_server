package cn.net.mall.auth.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 菜单树实体
 *
 * @date 2024/1/16 下午3:48
 */
@Data
public class MenuTreeDTO implements Serializable {

    /**
     * 菜单系统ID
     */
    @Schema(name = "菜单系统ID")
    private Long id;

    /**
     * 菜单名称
     */
    @Schema(name = "菜单名称")
    private String label;

    /**
     * 上级菜单ID
     */
    @Schema(name = "上级菜单ID")
    private Long pid;

    /**
     * 排序
     */
    @Schema(name = "排序")
    private Integer sort;

    /**
     * 图标
     */
    @Schema(name = "图标")
    private String icon;

    /**
     * 路由
     */
    @Schema(name = "路由")
    private String path;

    /**
     * 是否隐藏
     */
    @Schema(name = "是否隐藏")
    private Boolean hidden;

    /**
     * 是否外链 1：是 0：否
     */
    @Schema(name = "是否外链 1：是 0：否")
    private Integer isLink;

    /**
     * 类型 1：目录 2：菜单 3：按钮
     */
    @Schema(name = "类型 1：目录 2：菜单 3：按钮")
    private Integer type;

    /**
     * 功能权限
     */
    @Schema(name = "功能权限")
    private String permission;

    /**
     * url地址
     */
    @Schema(name = "url地址")
    private String url;

    /**
     * 组件
     */
    @Schema(name = "组件")
    private String component;

    /**
     * 创建时间
     */
    @Schema(name = "创建时间")
    private Date createTime;

    /**
     * 跳转
     */
    private String redirect;

    private Boolean alwaysShow;


    private MetaDTO meta;

    /**
     * 下一级菜单集合
     */
    private List<MenuTreeDTO> children;

    /**
     * 是否叶子节点
     */
    private Boolean leaf;

    /**
     * 下级菜单数量
     */
    private Integer subCount;

    private Boolean hasChildren;


    /**
     * 增加添加子菜单的方法
     *
     * @param menuTreeDTO 子菜单
     */
    public void addChildren(MenuTreeDTO menuTreeDTO) {
        if (Objects.isNull(children)) {
            children = Lists.newArrayList();
        }
        children.add(menuTreeDTO);
    }
}
