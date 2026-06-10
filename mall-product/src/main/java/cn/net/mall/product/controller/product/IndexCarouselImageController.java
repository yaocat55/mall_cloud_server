package cn.net.mall.product.controller.product;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.entity.IndexCarouselImageConditionEntity;
import cn.net.mall.product.entity.IndexCarouselImageEntity;
import cn.net.mall.product.service.IndexCarouselImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页轮播图 接口层
 *
 * @date 2024-08-21 18:34:11
 */
@Tag(name = "首页轮播图操作", description = "首页轮播图接口")
@AllArgsConstructor
@RestController
@RequestMapping("/v1/indexCarouselImage")
public class IndexCarouselImageController {

    private final IndexCarouselImageService indexCarouselImageService;

    /**
     * 通过id查询首页轮播图信息
     *
     * @param id 系统ID
     * @return 首页轮播图信息
     */
    @Operation(summary = "通过id查询首页轮播图信息", description = "通过id查询首页轮播图信息")
    @GetMapping("/findById")
    public IndexCarouselImageEntity findById(Long id) {
        return indexCarouselImageService.findById(id);
    }

    /**
     * 根据条件查询首页轮播图列表
     *
     * @param indexCarouselImageConditionEntity 条件
     * @return 首页轮播图列表
     */
    @Operation(summary = "根据条件查询首页轮播图列表", description = "根据条件查询首页轮播图列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<IndexCarouselImageEntity> searchByPage(@RequestBody IndexCarouselImageConditionEntity indexCarouselImageConditionEntity) {
        return indexCarouselImageService.searchByPage(indexCarouselImageConditionEntity);
    }


    /**
     * 添加首页轮播图
     *
     * @param indexCarouselImageEntity 首页轮播图实体
     * @return 影响行数
     */
    @Operation(summary = "添加首页轮播图", description = "添加首页轮播图")
    @PostMapping("/insert")
    public int insert(@RequestBody IndexCarouselImageEntity indexCarouselImageEntity) {
        return indexCarouselImageService.insert(indexCarouselImageEntity);
    }

    /**
     * 修改首页轮播图
     *
     * @param indexCarouselImageEntity 首页轮播图实体
     * @return 影响行数
     */
    @Operation(summary = "修改首页轮播图", description = "修改首页轮播图")
    @PostMapping("/update")
    public int update(@RequestBody IndexCarouselImageEntity indexCarouselImageEntity) {
        return indexCarouselImageService.update(indexCarouselImageEntity);
    }

    /**
     * 批量删除首页轮播图
     *
     * @param ids 首页轮播图ID集合
     * @return 影响行数
     */
    @Operation(summary = "批量删除首页轮播图", description = "批量删除首页轮播图")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return indexCarouselImageService.deleteByIds(ids);
    }
}
