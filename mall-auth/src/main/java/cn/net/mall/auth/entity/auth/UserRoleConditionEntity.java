package cn.net.mall.auth.entity.auth;

import cn.net.mall.entity.RequestPageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户角色关联查询条件实体
 *
 * @date 2024-01-08 17:18:19
 */
@Schema(name = "用户角色关联查询条件实体")
@Data
public class UserRoleConditionEntity extends RequestPageEntity {


    /**
     * ID
     */
    @Schema(name = "ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(name = "用户ID")
    private Long userId;

    /**
     * 用户ID集合
     */
    @Schema(name = "用户ID集合")
    private List<Long> userIdList;

    /**
     * 角色ID
     */
    @Schema(name = "角色ID")
    private Long roleId;
}
