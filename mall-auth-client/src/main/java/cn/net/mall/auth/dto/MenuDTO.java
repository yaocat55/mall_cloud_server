package cn.net.mall.auth.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 菜单 DTO
 * <p>
 * 用于 mall-admin-api 通过 Feign 调用获取菜单数据
 */
@Schema(description = "菜单 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MenuDTO extends BaseEntity {

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称", example = "测试数据")
    private String name;

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
     * 功能权限
     */
    @Schema(description = "功能权限", example = "-")
    private String permission;

    /**
     * 类型 1：目录 2：菜单 3：按钮
     */
    @Schema(description = "类型 1：目录 2：菜单 3：按钮", example = "1")
    private Integer type;

    /**
     * 组件
     */
    @Schema(description = "组件", example = "-")
    private String component;

    /**
     * url地址
     */
    @Schema(description = "url地址", example = "https://example.com/image.png")
    private String url;
}
