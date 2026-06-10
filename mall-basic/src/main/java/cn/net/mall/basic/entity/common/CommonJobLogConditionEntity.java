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
@Schema(name = "定时任务执行日志查询条件实体")
@Data
public class CommonJobLogConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(name = "ID")
    private Long id;

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

    /**
     * 创建人ID
     */
    @Schema(name = "创建人ID")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @Schema(name = "创建人名称")
    private String createUserName;

    /**
     * 创建日期
     */
    @Schema(name = "创建日期")
    private Date createTime;

    /**
     * 修改人ID
     */
    @Schema(name = "修改人ID")
    private Long updateUserId;

    /**
     * 修改人名称
     */
    @Schema(name = "修改人名称")
    private String updateUserName;

    /**
     * 修改时间
     */
    @Schema(name = "修改时间")
    private Date updateTime;

    /**
     * 是否删除 1：已删除 0：未删除
     */
    @Schema(name = "是否删除 1：已删除 0：未删除")
    private Integer isDel;
}
