package cn.net.mall.recommend.entity;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "ProductViewRecord信息")

public class ProductViewRecordConditionEntity extends RequestConditionEntity {
    @Schema(description = "系统ID列表")
    private List<Long> idList;
    @Schema(description = "系统ID")
    private Long id;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "商品ID")
    private Long productId;
    @Schema(description = "create User Id")
    private Long createUserId;
    @Schema(description = "create User Name")
    private String createUserName;
    @Schema(description = "update User Id")
    private Long updateUserId;
    @Schema(description = "update User Name")
    private String updateUserName;
    @Schema(description = "是否删除")
    private Integer isDel;
}
