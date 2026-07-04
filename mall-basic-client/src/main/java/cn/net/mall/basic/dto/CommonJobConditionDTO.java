package cn.net.mall.basic.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 定时任务查询条件 DTO
 *
 * @date 2025-07-03
 */
@Schema(description = "定时任务查询条件DTO")
@Data
public class CommonJobConditionDTO extends RequestConditionEntity {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "定时任务名称", example = "-")
    private String jobName;

    @Schema(description = "暂停状态 0：未暂停 1：已暂停", example = "0")
    private Integer pauseStatus;

    @Schema(description = "bean名称", example = "-")
    private String beanName;

    @Schema(description = "方法名称", example = "-")
    private String methodName;

    @Schema(description = "cron 表达式", example = "-")
    private String cronExpression;

    @Schema(description = "参数", example = "-")
    private String params;

    @Schema(description = "备注", example = "备注")
    private String remark;

    @Schema(description = "创建人ID", example = "1")
    private Long createUserId;

    @Schema(description = "创建人名称", example = "admin")
    private String createUserName;

    @Schema(description = "创建日期", example = "2024-01-01 00:00:00")
    private Date createTime;

    @Schema(description = "修改人ID", example = "1")
    private Long updateUserId;

    @Schema(description = "修改人名称", example = "admin")
    private String updateUserName;

    @Schema(description = "修改时间", example = "2024-01-01 00:00:00")
    private Date updateTime;

    @Schema(description = "是否删除 1：已删除 0：未删除", example = "0")
    private Integer isDel;
}
