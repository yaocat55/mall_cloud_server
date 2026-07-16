package cn.net.mall.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "批量删除 ID 列表")
public class IdsDTO {

    @Schema(description = "ID 列表", example = "[1, 2, 3]")
    private List<Long> ids;
}
