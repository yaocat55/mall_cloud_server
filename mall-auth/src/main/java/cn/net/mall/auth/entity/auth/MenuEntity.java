package cn.net.mall.auth.entity.auth;

import cn.net.mall.entity.BaseEntity;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 菜单实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-01-08 17:18:18
 */
@Schema(name = "菜单实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MenuEntity extends BaseEntity {


    /**
     * 菜单名称
     */
    @ExcelProperty(value = "菜单名称", index = 0)
    @Schema(name = "菜单名称")
    private String name;

    /**
     * 上级菜单ID
     */
    @ExcelProperty(value = "上级菜单ID", index = 1)
    @Schema(name = "上级菜单ID")
    private Long pid;

    /**
     * 排序
     */
    @ExcelProperty(value = "排序", index = 2)
    @Schema(name = "排序")
    private Integer sort;

    /**
     * 图标
     */
    @ExcelProperty(value = "图标", index = 3)
    @Schema(name = "图标")
    private String icon;

    /**
     * 路由
     */
    @ExcelProperty(value = "路由", index = 4)
    @Schema(name = "路由")
    private String path;

    /**
     * 是否隐藏
     */
    @ExcelProperty(value = "是否隐藏", index = 5)
    @Schema(name = "是否隐藏")
    private Boolean hidden;

    /**
     * 是否外链 1：是 0：否
     */
    @ExcelProperty(value = "是否外链", index = 6)
    @Schema(name = "是否外链 1：是 0：否")
    private Integer isLink;


    /**
     * 功能权限
     */
    @ExcelProperty(value = "功能权限", index = 7)
    @Schema(name = "功能权限")
    private String permission;

    /**
     * 类型 1：目录 2：菜单 3：按钮
     */
    @ExcelProperty(value = "类型", index = 8)
    @Schema(name = "类型 1：目录 2：菜单 3：按钮")
    private Integer type;

    /**
     * 组件
     */
    @ExcelProperty(value = "组件", index = 9)
    @Schema(name = "组件")
    private String component;

    /**
     * url地址
     */
    @ExcelProperty(value = "url地址", index = 10)
    @Schema(name = "url地址")
    private String url;
}
