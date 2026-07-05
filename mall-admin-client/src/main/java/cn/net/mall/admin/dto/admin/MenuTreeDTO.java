package cn.net.mall.admin.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 菜单树 DTO
 * 
* 用于 mall-admin-api 通过 Feign 调用获取菜单树数据
 */
@Schema(description = "菜单树 DTO")
@Data
public class MenuTreeDTO implements Serializable {

    /**
     * 菜单系统ID
     */
    @Schema(description = "菜单系统ID", example = "1")
    private Long id;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称", example = "标签")
    private String label;

    /**
     * 上级菜单ID
     */
    @Schema(description = "上级菜单ID", example = "0")
    private Long pid;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    private Integer sort;

    /**
     * 图标
     */
    @Schema(description = "图标", example = "-")
    private String icon;

    /**
     * 路由
     */
    @Schema(description = "路由", example = "-")
    private String path;

    /**
     * 是否隐藏
     */
    @Schema(description = "是否隐藏", example = "false")
    private Boolean hidden;

    /**
     * 是否外链 1：是 0：否
     */
    @Schema(description = "是否外链 1：是 0：否", example = "0")
    private Integer isLink;

    /**
     * 类型 1：目录 2：菜单 3：按钮
     */
    @Schema(description = "类型 1：目录 2：菜单 3：按钮", example = "1")
    private Integer type;

    /**
     * 功能权限
     */
    @Schema(description = "功能权限", example = "-")
    private String permission;

    /**
     * url地址
     */
    @Schema(description = "url地址", example = "https://example.com/image.png")
    private String url;

    /**
     * 组件
     */
    @Schema(description = "组件", example = "-")
    private String component;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-01-01 00:00:00")
    private Date createTime;

    /**
     * 下一级菜单集合
     */
    @Schema(description = "下一级菜单集合")
    private List children;

    /**
     * 是否叶子节点
     */
    @Schema(description = "是否叶子节点")
    private Boolean leaf;

    /**
     * 下级菜单数量
     */
    @Schema(description = "下级菜单数量", example = "0")
    private Integer subCount;

    /**
     * 是否有下一级
     */
    @Schema(description = "是否有下一级")
    private Boolean hasChildren;
}
