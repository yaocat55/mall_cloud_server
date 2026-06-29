package cn.net.mall.basic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 地区DTO
 *
 * @date 2024-10-04 11:43:55
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "地区DTO")
public class AreaDTO implements Serializable {
    /**
     * 系统ID
     */
    @Schema(description = "系统ID", example = "1")
    private Long id;

    /**
     * 上一级ID
     */
    @Schema(description = "上一级ID", example = "0")
    private Long parentId;

    /**
     * 名称
     */
    @Schema(description = "名称", example = "测试数据")
    private String name;

    /**
     * 拼音
     */
    @Schema(description = "拼音", example = "-")
    private String pinyin;

    /**
     * 全称
     */
    @Schema(description = "全称", example = "-")
    private String fullName;

    /**
     * 行政编码
     */
    @Schema(description = "行政编码", example = "CODE_001")
    private String code;

    /**
     * 级别
     */
    @Schema(description = "级别", example = "1")
    private Integer level;
}
