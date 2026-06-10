package cn.net.mall.entity.seckill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 秒杀商品详情实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-07-14 10:57:31
 */
@Schema(name = "秒杀商品详情实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SeckillProductDetailEntity extends ESSeckillProductEntity {

    /**
     * 商品详情
     */
    @Schema(name = "商品详情")
    private String detail;

    /**
     * 轮播图
     */
    private List<String> swiper;
}
