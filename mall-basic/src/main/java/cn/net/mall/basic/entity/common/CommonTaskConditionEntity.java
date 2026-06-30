

package cn.net.mall.basic.entity.common;

import cn.net.mall.entity.RequestPageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 任务查询条件实体
 *
 * @date 2024-01-29 17:31:17
 */
@Schema(description = "任务查询条件实体")
@Data
public class CommonTaskConditionEntity extends RequestPageEntity {


    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

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
    @Schema(description = "任务类型 1：通用excel导出", example = "1")
    private Integer type;

    /**
     * 执行状态 0：待执行 1：执行中 2：成功 3：失败
     */
    @Schema(description = "执行状态 0：待执行 1：执行中 2：成功 3：失败", example = "1")
    private Integer status;

    /**
     * 执行状态集合
     */
    @Schema(description = "状态列表")
    private List<Integer> statusList;

    /**
     * 失败次数
     */
    @Schema(description = "失败次数", example = "0")
    private Integer failureCount;

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

    /**
     * 业务类型 1：菜单 2：部门 3：角色 4：用户
     */
    @Schema(description = "业务类型 1：菜单 2：部门 3：角色 4：用户", example = "0")
    private Integer bizType;

    /**
     * 请求参数
     */
    @Schema(description = "请求参数", example = "-")
    private String requestParam;
}
