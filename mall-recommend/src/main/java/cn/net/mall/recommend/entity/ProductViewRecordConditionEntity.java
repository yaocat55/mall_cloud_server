package cn.net.mall.recommend.entity;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

import java.util.List;

@Data
public class ProductViewRecordConditionEntity extends RequestConditionEntity {
    private List<Long> idList;
    private Long id;
    private Long userId;
    private Long productId;
    private Long createUserId;
    private String createUserName;
    private Long updateUserId;
    private String updateUserName;
    private Integer isDel;
}
