package cn.net.mall.product.controller.product;

import io.swagger.v3.oas.annotations.tags.Tag;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.entity.IndexProductConditionEntity;
import cn.net.mall.product.entity.IndexProductEntity;
import cn.net.mall.product.service.IndexProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;

import java.util.List;

/**
 * 首页商品 接口层
 *
 * @date 2024-08-27 17:37:52
 */
@Tag(name = "首页商品操作", description = "首页商品接口")
@AllArgsConstructor
@RestController
@RequestMapping("/v1/indexProduct")
public class IndexProductController {

    private final IndexProductService indexProductService;

    private final Cache<String, ResponsePageEntity<IndexProductEntity>> indexProductCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    /**
     * 通过id查询首页商品信息
     *
     * @param id 系统ID
     * @return 首页商品信息
     */
    @Operation(summary = "通过id查询首页商品信息", description = "通过id查询首页商品信息")
    @GetMapping("/findById")
    public IndexProductEntity findById(Long id) {
        return indexProductService.findById(id);
    }

    /**
     * 根据条件查询首页商品列表
     *
     * @param indexProductConditionEntity 条件
     * @return 首页商品列表
     */
    @Operation(summary = "根据条件查询首页商品列表", description = "根据条件查询首页商品列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<IndexProductEntity> searchByPage(@RequestBody IndexProductConditionEntity indexProductConditionEntity) {
        String key = "index_product_" + indexProductConditionEntity.getType() + "_" 
                + indexProductConditionEntity.getPageNo() + "_" + indexProductConditionEntity.getPageSize();
        ResponsePageEntity<IndexProductEntity> result = indexProductCache.getIfPresent(key);
        if (result != null) {
            return result;
        }
        result = indexProductService.searchByPage(indexProductConditionEntity);
        indexProductCache.put(key, result);
        return result;
    }


    /**
     * 添加首页商品
     *
     * @param indexProductEntity 首页商品实体
     * @return 影响行数
     */
    @Operation(summary = "添加首页商品", description = "添加首页商品")
    @PostMapping("/insert")
    public int insert(@RequestBody IndexProductEntity indexProductEntity) {
        return indexProductService.insert(indexProductEntity);
    }

    /**
     * 修改首页商品
     *
     * @param indexProductEntity 首页商品实体
     * @return 影响行数
     */
    @Operation(summary = "修改首页商品", description = "修改首页商品")
    @PostMapping("/update")
    public int update(@RequestBody IndexProductEntity indexProductEntity) {
        return indexProductService.update(indexProductEntity);
    }

    /**
     * 批量删除首页商品
     *
     * @param ids 首页商品ID集合
     * @return 影响行数
     */
    @Operation(summary = "批量删除首页商品", description = "批量删除首页商品")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return indexProductService.deleteByIds(ids);
    }
}

