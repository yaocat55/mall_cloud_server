package cn.net.mall.auth.entity.auth;

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
@Schema(name = "用户头像查询条件实体")
@Data
public class UserAvatarConditionEntity extends RequestPageEntity {

    /**
     * 系统编号
     */
    @Schema(name = "")
    private Long id;


    /**
     * 系统编号集合
     */
    private List<Long> idList;

    /**
     * 文件名
     */
    @Schema(name = "文件名")
    private String fileName;

    /**
     * 路径
     */
    @Schema(name = "路径")
    private String path;

    /**
     * 大小
     */
    @Schema(name = "大小")
    private String fileSize;

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
