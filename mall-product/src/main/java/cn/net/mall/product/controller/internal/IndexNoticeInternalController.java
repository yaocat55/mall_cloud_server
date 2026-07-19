package cn.net.mall.product.controller.internal;

import cn.net.mall.product.entity.IndexNoticeEntity;
import cn.net.mall.product.service.IndexNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部首页公告接口
 *
* **调用方：**
 *   - admin-bff（管理后台）— 首页公告 CRUD 操作
 *
* **不对外暴露**，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-首页公告", description = "内部微服务：admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/indexNotice")
public class IndexNoticeInternalController {

    private final IndexNoticeService indexNoticeService;

    public IndexNoticeInternalController(IndexNoticeService indexNoticeService) {
        this.indexNoticeService = indexNoticeService;
    }

    @Operation(summary = "新增首页公告",
               description = "内部服务：由 admin-bff 通过 Feign 调用，新增首页公告记录")
    @PostMapping("/insert")
    public int insert(@RequestBody IndexNoticeEntity indexNoticeEntity) {
        return indexNoticeService.insert(indexNoticeEntity);
    }

    @Operation(summary = "修改首页公告",
               description = "内部服务：由 admin-bff 通过 Feign 调用，修改首页公告信息")
    @PostMapping("/update")
    public int update(@RequestBody IndexNoticeEntity indexNoticeEntity) {
        return indexNoticeService.update(indexNoticeEntity);
    }

    @Operation(summary = "批量删除首页公告",
               description = "内部服务：由 admin-bff 通过 Feign 调用，根据ID集合批量删除首页公告")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return indexNoticeService.deleteByIds(ids);
    }
}
