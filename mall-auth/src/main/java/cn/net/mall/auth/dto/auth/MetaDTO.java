package cn.net.mall.auth.dto.auth;

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
    private String icon;

    /**
     * 是否不缓存
     */
    private Boolean noCache;

    /**
     * 菜单标题
     */
    private String title;
}
