package cn.net.mall.basic.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 定时任务执行日志 DTO
 *
 * @date 2025-07-03
 */
@Schema(description = "定时任务执行日志DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonJobLogDTO extends BaseEntity {

    @Schema(description = "定时任务ID", example = "1")
    private Long jobId;

    @Schema(description = "定时任务名称", example = "-")
    private String jobName;

    @Schema(description = "执行状态 1：执行中 2：暂停 3：成功 4：失败", example = "0")
    private Integer runStatus;

    @Schema(description = "bean名称", example = "-")
    private String beanName;

    @Schema(description = "方法名称", example = "-")
    private String methodName;

    @Schema(description = "cron 表达式", example = "-")
    private String cronExpression;

    @Schema(description = "参数", example = "-")
    private String params;

    @Schema(description = "开始执行时间", example = "2024-01-01")
    private Date startTime;

    @Schema(description = "执行结束时间", example = "2024-01-01")
    private Date endTime;

    @Schema(description = "异常信息", example = "-")
    private String exception;
}
