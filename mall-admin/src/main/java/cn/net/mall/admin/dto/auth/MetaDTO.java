package cn.net.mall.admin.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 前端菜单所需meta实体
 *
 * @date 2024/1/16 下午6:10
 */
@Data
public class MetaDTO {

    /**
     * icon
     */
@Schema(example = "https://example.com/1.png")
    private String icon;

    /**
     * 是否不缓存
     */
@Schema(example = "true")
    private Boolean noCache;

    /**
     * 菜单标题
     */
@Schema(example = "标题")
    private String title;
}
