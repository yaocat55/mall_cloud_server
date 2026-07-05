package cn.net.mall.admin.dto;

import cn.net.mall.product.dto.CategoryDTO;
import cn.net.mall.product.dto.ProductDTO;
import cn.net.mall.product.dto.ProductDetailDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 商品编辑页聚合数据 DTO
 * 
* 由 AdminProductController 聚合返回，包含商品编辑页所需的所有数据
 */
@Data
@Schema(description = "商品编辑页聚合数据")
public class ProductEditDataDTO {

    @Schema(description = "商品基本信息（ID、名称、价格等）")
    private ProductDTO product;

    @Schema(description = "商品详情信息（富文本描述、属性等）")
    private ProductDetailDTO detail;

    @Schema(description = "分类树列表，用于选择商品分类")
    private List categoryTree;

    @Schema(description = "品牌列表，用于选择商品品牌（TODO: 待实现）")
    private List<?> brands;

    @Schema(description = "单位列表，用于选择商品单位（TODO: 待实现）")
    private List<?> units;
}
