package cn.net.mall.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 请求条件实体
 *
 * @date 2024/1/25 下午4:43
 */
@Data
public class RequestConditionEntity extends RequestPageEntity {


    /**
     * 创建日期范围
     */
    @Schema(description = "创建日期范围", example = "-")
    @Schema(description = "betweenTime", example = "string")
    private List<String> betweenTime;

    /**
     * 创建开始时间
     */
    @Schema(description = "createBeginTime", example = "string")
    private String createBeginTime;

    /**
     * 创建结束时间
     */
    @Schema(description = "createEndTime", example = "string")
    private String createEndTime;

    /**
     * 自定义excel表头列表
     */
    @Schema(description = "customizeColumnNameList", example = "string")
    private List<String> customizeColumnNameList;

    /**
     * 查询条件
     */
    @Schema(description = "blurry", example = "string")
    private String blurry;
}
