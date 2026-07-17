package cn.net.mall.inventory.client;

import cn.net.mall.inventory.client.fallback.InventoryFeignFallbackFactory;
import cn.net.mall.inventory.dto.*;
import cn.net.mall.inventory.dto.RowsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 库存 Feign 客户端.
 */
@FeignClient(name = "mall-inventory-api", path = "/v1/inventory",
        fallbackFactory = InventoryFeignFallbackFactory.class)
public interface InventoryFeignClient {

    @PostMapping("/freeze")
    RowsDTO freeze(@Valid @RequestBody InventoryFreezeDTO dto);

    @PostMapping("/confirm")
    RowsDTO confirm(@Valid @RequestBody InventoryConfirmDTO dto);

    @PostMapping("/unfreeze")
    RowsDTO unfreeze(@Valid @RequestBody InventoryUnfreezeDTO dto);

    @PostMapping("/return")
    RowsDTO returnStock(@Valid @RequestBody InventoryReturnDTO dto);

    @PostMapping("/inbound")
    RowsDTO inbound(@Valid @RequestBody InventoryInboundDTO dto);

    @GetMapping("/{productId}")
    InventoryDTO getByProductId(@PathVariable Long productId);

    @PostMapping("/batch")
    List<InventoryDTO> getByProductIds(@RequestBody List<Long> productIds);
}
