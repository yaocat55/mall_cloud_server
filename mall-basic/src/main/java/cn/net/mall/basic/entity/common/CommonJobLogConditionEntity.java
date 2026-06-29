package cn.net.mall.basic.entity.common;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 定时任务执行日志查询条件实体
 *
 * @date 2024-04-30 15:09:07
 */
@Schema(description = "定时任务执行日志查询条件实体")
@Data
public class CommonJobLogConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

    /**
     * 定时任务ID
     */
    @Schema(description = "定时任务ID", example = "1")
    private Long jobId;

    /**
     * 定时任务名称
     */
    @Schema(description = "定时任务名称", example = "-")
    private String jobName;

    /**
     * 执行状态 1：执行中 2：暂停 3：成功 4：失败
     */
    @Schema(description = "执行状态 1：执行中 2：暂停 3：成功 4：失败", example = "0")
    private Integer runStatus;

    /**
     * bean名称
     */
    @Schema(description = "bean名称", example = "-")
    private String beanName;

    /**
     * 方法名称
     */
    @Schema(description = "方法名称", example = "-")
    private String methodName;

    /**
     * cron 表达式
     */
    @Schema(description = "cron 表达式", example = "-")
    private String cronExpression;

    /**
     * 参数
     */
    @Schema(description = "参数", example = "-")
    private String params;

    /**
     * 开始执行时间
     */
    @Schema(description = "开始执行时间", example = "2024-01-01")
    private Date startTime;

    /**
     * 执行结束时间
     */
    @Schema(description = "执行结束时间", example = "2024-01-01")
    private Date endTime;

    /**
     * 异常信息
     */
    @Schema(description = "异常信息", example = "-")
    private String exception;

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
