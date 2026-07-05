package cn.net.mall.basic.entity.common;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-01-29 17:31:17
 */
@Schema(description = "任务实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonTaskEntity extends BaseEntity {


    /**
     * 任务名称
     */
    @Schema(description = "任务名称", example = "测试数据")
    private String name;

    /**
     * 下载文件地址
     */
    @Schema(description = "下载文件地址", example = "-")
    private String fileUrl;

    /**
     * 任务类型 1：通用excel导出
     */
    @Schema(description = "任务类型 1：通用excel导出 2：发邮件", example = "1")
    private Integer type;

    /**
     * 执行状态 0：待执行 1：执行中 2：成功 3：失败
     */
    @Schema(description = "执行状态 0：待执行 1：执行中 2：成功 3：失败", example = "1")
    private Integer status;

    /**
     * 失败次数
     */
    @Schema(description = "失败次数", example = "0")
    private Integer failureCount;

    /**
     * 业务类型
     * 任务类型时通用excel导出时
     * 1：菜单 2：部门 3：角色 4：用户
     * 
* 任务类型时发邮件时
     * 1：异地登录
     */
    @Schema(description = "业务类型 1：菜单 2：部门 3：角色 4：用户", example = "0")
    private Integer bizType;

    /**
     * 请求参数
     */
    @Schema(description = "请求参数", example = "-")
    private String requestParam;
}
