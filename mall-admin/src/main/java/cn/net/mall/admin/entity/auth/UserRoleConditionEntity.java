package cn.net.mall.admin.entity.auth;

import cn.net.mall.entity.RequestPageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户角色关联查询条件实体
 *
 * @date 2024-01-08 17:18:19
 */
@Schema(description = "用户角色关联查询条件实体")
@Data
public class UserRoleConditionEntity extends RequestPageEntity {


    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /**
     * 用户ID集合
     */
    @Schema(description = "用户ID集合", example = "0")
    private List<Long> userIdList;

    /**
     * 角色ID
     */
    @Schema(description = "角色ID", example = "1")
    private Long roleId;
}
