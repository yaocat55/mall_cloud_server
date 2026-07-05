package cn.net.mall.admin.entity.auth;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 菜单查询条件实体
 *
 * @date 2024-01-08 17:18:18
 */
@Schema(description = "菜单查询条件实体")
@Data
public class MenuConditionEntity extends RequestConditionEntity {

    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

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
     * 上级菜单ID集合
     */
    @Schema(description = "上级菜单ID集合", example = "0")
    private List<Long> pidList;

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
    @Schema(description = "是否隐藏", example = "0")
    private Integer hidden;

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
     * 创建人ID
     */
    @Schema(description = "创建人ID", example = "1")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @Schema(description = "创建人名称", example = "admin")
    private String createUserName;


    /**
     * 创建日期
     */
    @Schema(description = "创建日期", example = "2024-01-01 00:00:00")
    private Date createTime;

    /**
     * 修改人ID
     */
    @Schema(description = "修改人ID", example = "1")
    private Long updateUserId;

    /**
     * 修改人名称
     */
    @Schema(description = "修改人名称", example = "admin")
    private String updateUserName;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间", example = "2024-01-01 00:00:00")
    private Date updateTime;

    /**
     * 是否删除 1：已删除 0：未删除
     */
    @Schema(description = "是否删除 1：已删除 0：未删除", example = "0")
    private Integer isDel;
}
