package cn.net.mall.basic.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 敏感词 DTO
 *
 * @date 2025-07-03
 */
@Schema(description = "敏感词DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonSensitiveWordDTO extends BaseEntity {

    @Schema(description = "类型（1-政治 2-广告 3-色情 4-违法 5-网址）", allowableValues = {"1", "2", "3", "4", "5"}, example = "1")
    private Integer type;

    @Schema(description = "名称", example = "-")
    private String word;
}
