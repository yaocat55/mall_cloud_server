package cn.net.mall.auth.entity.auth;

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
@Schema(name = "菜单查询条件实体")
@Data
public class MenuConditionEntity extends RequestConditionEntity {

    /**
     * ID
     */
    @Schema(name = "ID")
    private Long id;

    /**
     * 菜单名称
     */
    @Schema(name = "菜单名称")
    private String name;

    /**
     * 上级菜单ID
     */
    @Schema(name = "上级菜单ID")
    private Long pid;

    /**
     * 上级菜单ID集合
     */
    @Schema(name = "上级菜单ID集合")
    private List<Long> pidList;

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
    private Integer hidden;

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
     * 创建人ID
     */
    @Schema(name = "创建人ID")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @Schema(name = "创建人名称")
    private String createUserName;


    /**
     * 创建日期
     */
    @Schema(name = "创建日期")
    private Date createTime;

    /**
     * 修改人ID
     */
    @Schema(name = "修改人ID")
    private Long updateUserId;

    /**
     * 修改人名称
     */
    @Schema(name = "修改人名称")
    private String updateUserName;

    /**
     * 修改时间
     */
    @Schema(name = "修改时间")
    private Date updateTime;

    /**
     * 是否删除 1：已删除 0：未删除
     */
    @Schema(name = "是否删除 1：已删除 0：未删除")
    private Integer isDel;
}
