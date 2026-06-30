package cn.net.mall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * ES 公共实体
 *
 * @date 2024/5/14 下午4:32
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EsBaseEntity implements Serializable {

    /**
     * ID
     */
    @Schema(description = "系统ID")
    private String id;

    /**
     * 数据
     */
    private Map<String, Object> data;
}
