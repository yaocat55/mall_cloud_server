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

    @Schema(description = "类型 1:广告 2:政治 3：违法 4：色情 5：网址", example = "1")
    private Integer type;

    @Schema(description = "名称", example = "-")
    private String word;
}
