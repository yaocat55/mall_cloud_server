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
@Schema(name = "地区DTO")
public class AreaDTO implements Serializable {
    /**
     * 系统ID
     */
    @Schema(name = "系统ID")
    private Long id;

    /**
     * 上一级ID
     */
    @Schema(name = "上一级ID")
    private Long parentId;

    /**
     * 名称
     */
    @Schema(name = "名称")
    private String name;

    /**
     * 拼音
     */
    @Schema(name = "拼音")
    private String pinyin;

    /**
     * 全称
     */
    @Schema(name = "全称")
    private String fullName;

    /**
     * 行政编码
     */
    @Schema(name = "行政编码")
    private String code;

    /**
     * 级别
     */
    @Schema(name = "级别")
    private Integer level;
}
