package cn.net.mall.basic.entity.common;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 图片查询条件实体
 *
 * @date 2024-07-03 16:43:09
 */
@Schema(description = "图片查询条件实体")
@Data
public class CommonPhotoConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

    /**
     * 图片分组ID
     */
    @Schema(description = "图片分组ID", example = "0")
    private Long photoGroupId;

    /**
     * 图片名称
     */
    @Schema(description = "图片名称", example = "测试数据")
    private String name;

    /**
     * 图片url
     */
    @Schema(description = "图片url", example = "https://example.com/image.png")
    private String url;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID", example = "1")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @Schema(description = "创建人名称", example = "admin")
    private String createUserName;

    /**
     * 创建日期
     */
    @Schema(description = "创建日期", example = "2024-01-01 00:00:00")
    private Date createTime;

    /**
     * 修改人ID
     */
    @Schema(description = "修改人ID", example = "1")
    private Long updateUserId;

    /**
     * 修改人名称
     */
    @Schema(description = "修改人名称", example = "admin")
    private String updateUserName;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间", example = "2024-01-01 00:00:00")
    private Date updateTime;

    /**
     * 是否删除 1：已删除 0：未删除
     */
    @Schema(description = "是否删除 1：已删除 0：未删除", example = "0")
    private Integer isDel;
}
