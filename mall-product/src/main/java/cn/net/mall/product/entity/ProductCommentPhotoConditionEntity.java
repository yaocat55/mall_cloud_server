package cn.net.mall.product.entity;

import cn.net.mall.entity.RequestPageEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "商品评论图片")

public class ProductCommentPhotoConditionEntity extends RequestPageEntity {
    private Long id;
    private Long commentId;
    private List<Long> commentIdList;
    private Long productId;
    private String url;
    private Integer sort;
    private Long createUserId;
    private String createUserName;
    private Date createTime;
    private Long updateUserId;
    private String updateUserName;
    private Date updateTime;
    private Integer isDel;
}
