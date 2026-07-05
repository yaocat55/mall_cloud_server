package cn.net.mall.admin.entity.auth;

import cn.net.mall.entity.RequestPageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户头像查询条件实体
 *
 * @date 2024-01-08 17:18:18
 */
@Schema(description = "用户头像查询条件实体")
@Data
public class UserAvatarConditionEntity extends RequestPageEntity {

    /**
     * 系统编号
     */
    @Schema(description = "", example = "1")
    private Long id;


    /**
     * 系统编号集合
     */
    @Schema(description = "系统ID列表")
    private List<Long> idList;

    /**
     * 文件名
     */
    @Schema(description = "文件名", example = "-")
    private String fileName;

    /**
     * 路径
     */
    @Schema(description = "路径", example = "-")
    private String path;

    /**
     * 大小
     */
    @Schema(description = "大小", example = "-")
    private String fileSize;

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
