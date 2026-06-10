package cn.net.mall.order.dto;

import cn.net.mall.annotation.ValidSensitiveWordField;
import lombok.Data;

import java.util.List;

@Data
public class OrderEvaluateProductItemDTO {
    private String productId;
    private Integer rating;
    @ValidSensitiveWordField
    private String content;
    private List<String> images;
    private Boolean isAnonymous;
}
