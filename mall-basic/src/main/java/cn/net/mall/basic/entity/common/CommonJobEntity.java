package cn.net.mall.basic.entity.common;

import cn.net.mall.basic.enums.CommonJobOperateTypeEnum;
import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 定时任务实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-04-30 15:09:06
 */
@Schema(description = "定时任务实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonJobEntity extends BaseEntity {


    /**
     * 定时任务名称
     */
    @Schema(description = "定时任务名称", example = "-")
    private String jobName;

    /**
     * 暂停状态 0：未暂停 1：已暂停
     */
    @Schema(description = "暂停状态 0：未暂停 1：已暂停", example = "false")
    private Boolean pauseStatus;

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
     * 备注
     */
    @Schema(description = "备注", example = "备注")
    private String remark;

    /**
     * 操作类型
     */
    @Schema(description = "操作类型")
    private CommonJobOperateTypeEnum operateTypeEnum;
}
