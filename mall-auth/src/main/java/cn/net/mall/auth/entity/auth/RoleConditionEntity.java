package cn.net.mall.auth.entity.auth;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 角色查询条件实体
 *
 * @date 2024-01-08 17:18:18
 */
@Schema(name = "角色查询条件实体")
@Data
public class RoleConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(name = "ID")
    private Long id;

    /**
     * 名称
     */
    @Schema(name = "名称")
    private String name;

    /**
     * 备注
     */
    @Schema(name = "备注")
    private String remark;

    /**
     * 数据权限
     */
    @Schema(name = "数据权限")
    private String dataScope;

    /**
     * 角色级别
     */
    @Schema(name = "角色级别")
    private Integer level;

    /**
     * 功能权限
     */
    @Schema(name = "功能权限")
    private String permission;

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

    /**
     * id集合
     */
    private List<Long> idList;

    /**
     * 查询条件
     */
    private String blurry;
}
