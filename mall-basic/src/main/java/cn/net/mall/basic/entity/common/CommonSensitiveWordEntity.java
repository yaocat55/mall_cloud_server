package cn.net.mall.basic.entity.common;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 敏感词实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-05-18 21:09:00
 */
@Schema(name = "敏感词实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonSensitiveWordEntity extends BaseEntity {


    /**
     * 类型 1:广告 2:政治 3：违法 4：色情 5：网址
     */
    @Schema(name = "类型 1:广告 2:政治 3：违法 4：色情 5：网址")
    private Integer type;

    /**
     * 名称
     */
    @Schema(name = "名称")
    private String word;
}
