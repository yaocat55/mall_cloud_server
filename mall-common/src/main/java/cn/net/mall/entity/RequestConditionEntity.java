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
    @Schema(name = "创建日期范围")
    private List<String> betweenTime;

    /**
     * 创建开始时间
     */
    private String createBeginTime;

    /**
     * 创建结束时间
     */
    private String createEndTime;

    /**
     * 自定义excel表头列表
     */
    private List<String> customizeColumnNameList;

    /**
     * 查询条件
     */
    private String blurry;
}
