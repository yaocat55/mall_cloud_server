package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * ID 响应 DTO.
 *
 * <p>用于创建类操作返回新记录 ID 的场景。</p>
 */
@Data
@Schema(description = "ID 响应")
public class IdDTO {

    @Schema(description = "记录 ID", example = "1")
    private Long id;

    public IdDTO() {
    }

    public IdDTO(Long id) {
        this.id = id;
    }
}
