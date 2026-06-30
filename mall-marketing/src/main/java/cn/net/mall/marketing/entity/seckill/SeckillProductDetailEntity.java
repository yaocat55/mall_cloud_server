package cn.net.mall.marketing.entity.seckill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "秒杀商品详情实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SeckillProductDetailEntity extends ESSeckillProductEntity {

    @Schema(description = "商品详情", example = "-")
    private String detail;

    @Schema(description = "swiper")
    private List<String> swiper;
}
