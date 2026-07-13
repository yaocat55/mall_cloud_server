package cn.net.mall.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 影响行数响应 DTO.
 *
 * <p>替代 {@code int} 原始类型返回，使得 Swagger 能展示有意义的响应示例。</p>
 * <p>用于 insert / update / delete 等操作，表示受影响的行数。</p>
 *
 * <pre>{@code
 * // 改前
 * public int insert(@RequestBody MenuEntity entity)
 * // 改后
 * public RowsDTO insert(@RequestBody MenuEntity entity)
 * }</pre>
 */
@Data
@Schema(description = "影响行数响应")
public class RowsDTO {

    @Schema(description = "影响行数", example = "1")
    private int rows;

    public RowsDTO() {
    }

    public RowsDTO(int rows) {
        this.rows = rows;
    }
}
