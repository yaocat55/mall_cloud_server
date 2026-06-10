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
@Schema(name = "定时任务执行日志实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonJobLogEntity extends BaseEntity {


    /**
     * 定时任务ID
     */
    @Schema(name = "定时任务ID")
    private Long jobId;

    /**
     * 定时任务名称
     */
    @Schema(name = "定时任务名称")
    private String jobName;

    /**
     * 执行状态 1：执行中 2：暂停 3：成功 4：失败
     */
    @Schema(name = "执行状态 1：执行中 2：暂停 3：成功 4：失败")
    private Integer runStatus;

    /**
     * bean名称
     */
    @Schema(name = "bean名称")
    private String beanName;

    /**
     * 方法名称
     */
    @Schema(name = "方法名称")
    private String methodName;

    /**
     * cron 表达式
     */
    @Schema(name = "cron 表达式")
    private String cronExpression;

    /**
     * 参数
     */
    @Schema(name = "参数")
    private String params;

    /**
     * 开始执行时间
     */
    @Schema(name = "开始执行时间")
    private Date startTime;

    /**
     * 执行结束时间
     */
    @Schema(name = "执行结束时间")
    private Date endTime;

    /**
     * 异常信息
     */
    @Schema(name = "异常信息")
    private String exception;
}
