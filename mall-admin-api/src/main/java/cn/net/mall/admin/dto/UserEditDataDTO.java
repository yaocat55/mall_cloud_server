package cn.net.mall.admin.dto;

import cn.net.mall.auth.dto.UserDTO;
import cn.net.mall.auth.dto.RoleDTO;
import cn.net.mall.auth.dto.JobDTO;
import cn.net.mall.auth.dto.auth.DeptTreeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户编辑页聚合数据 DTO
 * <p>
 * 由 AdminUserController 聚合返回，包含管理后台用户编辑页所需的所有数据
 */
@Data
@Schema(description = "用户编辑页聚合数据")
public class UserEditDataDTO {

    @Schema(description = "用户基本信息（ID、用户名、手机号、邮箱、头像等）")
    private UserDTO user;

    @Schema(description = "所有角色列表，用于角色分配下拉框")
    private List<RoleDTO> roles;

    @Schema(description = "部门树列表，用于选择所属部门")
    private List<DeptTreeDTO> deptTree;

    @Schema(description = "所有岗位列表，用于岗位分配下拉框")
    private List<JobDTO> jobs;
}
