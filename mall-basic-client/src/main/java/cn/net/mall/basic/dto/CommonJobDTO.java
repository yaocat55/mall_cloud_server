package cn.net.mall.basic.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 定时任务 DTO
 *
 * @date 2025-07-03
 */
@Schema(description = "定时任务DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonJobDTO extends BaseEntity {

    @Schema(description = "定时任务名称", example = "-")
    private String jobName;

    @Schema(description = "暂停状态 0：未暂停 1：已暂停", example = "false")
    private Boolean pauseStatus;

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

    @Schema(description = "操作类型", example = "-")
    private String operateType;
}
