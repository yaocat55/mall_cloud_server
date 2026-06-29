package cn.net.mall.product.controller.product;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.entity.ProductViewRecordConditionEntity;
import cn.net.mall.product.entity.ProductViewRecordEntity;
import cn.net.mall.product.mapper.ProductViewRecordMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "浏览记录管理", description = "管理后台：用户浏览记录")
@AllArgsConstructor
@RestController
@RequestMapping("/v1/productViewRecord")
public class ProductViewRecordController {

    private final ProductViewRecordMapper productViewRecordMapper;

    @Operation(summary = "根据条件查询商品浏览记录列表", description = "根据条件查询商品浏览记录列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<ProductViewRecordEntity> searchByPage(@RequestBody @NotNull ProductViewRecordConditionEntity condition) {
        List<ProductViewRecordEntity> data = productViewRecordMapper.searchByCondition(condition);
        int total = productViewRecordMapper.searchCount(condition);
        return ResponsePageEntity.build(condition, total, data);
    }

    @Operation(summary = "仅返回商品浏览记录数据列表（无总数）", description = "仅返回商品浏览记录数据列表（无总数）")
    @PostMapping("/searchList")
    public List<ProductViewRecordEntity> searchList(@RequestBody @NotNull ProductViewRecordConditionEntity condition) {
        return productViewRecordMapper.searchByCondition(condition);
    }
}
