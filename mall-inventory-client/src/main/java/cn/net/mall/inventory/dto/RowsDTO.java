package cn.net.mall.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 受影响行数 DTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "受影响行数")
public class RowsDTO {
    @Schema(description = "影响行数", example = "1")
    private Integer rows;
}
