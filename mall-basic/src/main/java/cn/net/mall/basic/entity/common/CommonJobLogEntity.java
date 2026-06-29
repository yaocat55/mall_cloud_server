package cn.net.mall.basic.entity.common;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 定时任务执行日志实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-04-30 15:09:07
 */
@Schema(description = "定时任务执行日志实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonJobLogEntity extends BaseEntity {


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
}
