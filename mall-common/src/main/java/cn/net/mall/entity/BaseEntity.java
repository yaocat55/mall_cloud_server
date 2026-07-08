package cn.net.mall.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 公共实体类
 *
 * @date 2024/1/4 下午3:32
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseEntity implements Serializable {

    /**
     * 系统ID
     */
    @Schema(description = "系统ID", example = "13")
    @Schema(description = "id", example = "0")
    private Long id;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID", example = "1")
    @Schema(description = "createUserId", example = "0")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @Schema(description = "创建人名称", example = "admin")
    @Schema(description = "createUserName", example = "string")
    private String createUserName;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-01-01 00:00:00")
    @Schema(description = "createTime", example = "2024-01-01")
    private Date createTime;

    /**
     * 修改人ID
     */
    @Schema(description = "修改人ID", example = "1")
    @Schema(description = "updateUserId", example = "0")
    private Long updateUserId;

    /**
     * 修改人名称
     */
    @Schema(description = "修改人名称", example = "admin")
    @Schema(description = "updateUserName", example = "string")
    private String updateUserName;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间", example = "2024-01-01 00:00:00")
    @Schema(description = "updateTime", example = "2024-01-01")
    private Date updateTime;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除 0-否 1-是", example = "0")
    @Schema(description = "isDel", example = "0")
    private Integer isDel;
}
