package cn.net.mall.product.entity;

import cn.net.mall.entity.RequestPageEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "商品评论图片")

public class ProductCommentPhotoConditionEntity extends RequestPageEntity {
    @Schema(description = "系统ID")
    private Long id;
    @Schema(description = "comment Id")
    private Long commentId;
    @Schema(description = "comment Id List")
    private List<Long> commentIdList;
    @Schema(description = "商品ID")
    private Long productId;
    @Schema(description = "url")
    private String url;
    @Schema(description = "排序")
    private Integer sort;
    @Schema(description = "create User Id")
    private Long createUserId;
    @Schema(description = "create User Name")
    private String createUserName;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "update User Id")
    private Long updateUserId;
    @Schema(description = "update User Name")
    private String updateUserName;
    @Schema(description = "修改时间")
    private Date updateTime;
    @Schema(description = "是否删除")
    private Integer isDel;
}
