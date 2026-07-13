package cn.net.mall.order.client;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.order.client.fallback.OrderFeignFallbackFactory;
import cn.net.mall.order.dto.OrderConditionDTO;
import cn.net.mall.order.dto.OrderDTO;
import cn.net.mall.order.dto.OrderConfirmReqDTO;
import cn.net.mall.order.dto.OrderConfirmRespDTO;
import cn.net.mall.order.dto.OrderSubmitDTO;
import cn.net.mall.order.dto.OrderSubmitRespDTO;
import cn.net.mall.order.dto.OrderOperateDTO;
import cn.net.mall.order.dto.OrderTradeCountDTO;
import cn.net.mall.order.dto.TradeItemReqDTO;
import cn.net.mall.order.dto.TradeItemDTO;
import cn.net.mall.order.dto.TradeDetailDTO;
import cn.net.mall.order.dto.OrderReturnApplyDTO;
import cn.net.mall.order.dto.OrderReturnConditionDTO;
import cn.net.mall.order.dto.RowsDTO;
import cn.net.mall.order.dto.OrderEvaluateDTO;
import cn.net.mall.order.dto.IdDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import static cn.net.mall.order.constant.AppConstant.ORDER_SERVICE_NAME;

/**
 * 订单服务 Feign 客户端
 * 
* **调用方：**
 * 
*   - mall-pay（支付服务）— 支付回调时查询订单信息
 *   - mall-mobile-api（BFF 服务）— 移动端接口聚合
 * 
*/
@FeignClient(value = ORDER_SERVICE_NAME, contextId = "orderFeignClient",
        fallbackFactory = OrderFeignFallbackFactory.class)
public interface OrderFeignClient {

    /**
     * 创建订单
     * 
调用方：mall-mobile-api（BFF 服务）
*/
    @Operation(summary = "创建订单", description = "由 mall-mobile-api(BFF) 调用，创建新订单")
    @PostMapping("/v1/internal/trade/create")
    Long create(@RequestBody OrderDTO orderDTO);

    /**
     * 根据 ID 查询订单
     * 
调用方：mall-pay（支付服务）
*/
    @Operation(summary = "根据ID查询订单", description = "由 mall-pay 调用，根据订单ID获取订单信息")
    @GetMapping("/v1/mobile/trade/getDetail/{id}")
    OrderDTO findById(@PathVariable("id") Long id);

    /**
     * 分页查询订单
     * 
调用方：mall-mobile-api（BFF 服务）
*/
    @Operation(summary = "分页查询订单", description = "由 mall-mobile-api(BFF) 调用，分页查询订单列表")
    @PostMapping("/v1/mobile/trade/search")
    ResponsePageEntity<OrderDTO> search(@RequestBody OrderConditionDTO orderConditionDTO);

    /**
     * 修改订单
     * 
调用方：mall-mobile-api（BFF 服务）
*/
    @Operation(summary = "修改订单", description = "由 mall-mobile-api(BFF) 调用，修改订单信息")
    @PostMapping("/v1/mobile/trade/update")
    RowsDTO update(@RequestBody OrderDTO orderDTO);

    /**
     * 批量删除订单
     */
    @Operation(summary = "批量删除订单")
    @PostMapping("/v1/mobile/trade/delete")
    RowsDTO delete(@RequestBody List<Long> ids);

    /**
     * 订单确认 / 预览
     */
    @Operation(summary = "订单确认/预览")
    @PostMapping("/v1/mobile/trade/confirm")
    OrderConfirmRespDTO confirm(@RequestBody OrderConfirmReqDTO req);

    /**
     * 提交订单
     */
    @Operation(summary = "提交订单")
    @PostMapping("/v1/mobile/trade/submit")
    OrderSubmitRespDTO submit(@RequestBody OrderSubmitDTO submitDTO);

    /**
     * 获取用户订单数量统计
     * 
调用方：mall-mobile-api（BFF 服务）
*/
    @Operation(summary = "获取用户订单数量统计", description = "由 mall-mobile-api(BFF) 调用")
    @GetMapping("/v1/mobile/trade/getUserOrderCount")
    OrderTradeCountDTO getUserOrderCount();

    /**
     * 取消订单
     */
    @Operation(summary = "取消订单")
    @PostMapping("/v1/mobile/trade/cancel")
    RowsDTO cancel(@RequestBody OrderOperateDTO operateDTO);

    /**
     * 确认收货
     */
    @Operation(summary = "确认收货")
    @PostMapping("/v1/mobile/trade/confirmReceive")
    RowsDTO confirmReceive(@RequestBody OrderOperateDTO operateDTO);

    /**
     * 订单评价
     */
    @Operation(summary = "订单评价")
    @PostMapping("/v1/mobile/trade/evaluate")
    RowsDTO evaluate(@RequestBody OrderEvaluateDTO evaluateDTO);

    /**
     * 获取订单明细
     */
    @Operation(summary = "获取订单明细")
    @PostMapping("/v1/mobile/trade/getTradeItem")
    TradeItemDTO getTradeItem(@RequestBody TradeItemReqDTO req);

    /**
     * 获取用户订单数量统计（备用）
     */
    @Operation(summary = "获取用户订单数量统计")
    @GetMapping("/v1/mobile/trade/getUserOrderTradeCount")
    OrderTradeCountDTO getUserOrderTradeCount();

    /**
     * 根据编码查询订单详情
     * 
调用方：mall-pay（支付服务）— 支付回调时根据订单编码获取详情
*/
    @Operation(summary = "根据编码查询订单详情", description = "由 mall-pay 支付回调时调用，根据订单编码获取详情")
    @GetMapping("/v1/internal/trade/getDetailByCode/{code}")
    TradeDetailDTO getDetailByCode(@PathVariable("code") String code);

    /**
     * 根据编码查询订单
     * 
调用方：mall-pay（支付服务）— 支付回调时根据订单编码获取基本信息
*/
    @Operation(summary = "根据编码查询订单", description = "由 mall-pay 支付回调时调用，根据订单编码获取基本信息")
    @GetMapping("/v1/internal/trade/getTrade/{code}")
    OrderDTO getTrade(@PathVariable("code") String code);

    /**
     * 申请退货
     */
    @Operation(summary = "申请退货")
    @PostMapping("/v1/mobile/trade/return/apply")
    IdDTO applyReturn(@RequestBody OrderReturnApplyDTO dto);

    /**
     * 查询退货列表
     */
    @Operation(summary = "查询退货列表")
    @PostMapping("/v1/mobile/trade/return/search")
    ResponsePageEntity<OrderReturnApplyDTO> searchReturn(@RequestBody OrderReturnConditionDTO condition);

    // ========== 交易配送地址 (tradeDeliveryAddress) 管理端 ==========

    @Operation(summary = "分页查询交易配送地址（管理端）")
    @PostMapping("/v1/tradeDeliveryAddress/searchByPage")
    ResponsePageEntity<?> searchDeliveryAddressPage(@RequestBody Map<String, Object> condition);

    @Operation(summary = "新增交易配送地址（管理端）")
    @PostMapping("/v1/tradeDeliveryAddress/insert")
    RowsDTO insertDeliveryAddress(@RequestBody Object entity);

    @Operation(summary = "修改交易配送地址（管理端）")
    @PostMapping("/v1/tradeDeliveryAddress/update")
    RowsDTO updateDeliveryAddress(@RequestBody Object entity);

    @Operation(summary = "删除交易配送地址（管理端）")
    @PostMapping("/v1/tradeDeliveryAddress/deleteByIds")
    RowsDTO deleteDeliveryAddressByIds(@RequestBody List<Long> ids);

    @Operation(summary = "查询交易配送地址详情（管理端）")
    @GetMapping("/v1/tradeDeliveryAddress/findById")
    Object findDeliveryAddressById(@RequestParam("id") Long id);

    // ========== 退货/退款 (return) 管理端 ==========

    @Operation(summary = "分页查询退货列表（管理端）")
    @PostMapping("/v1/trade/return/searchByPage")
    ResponsePageEntity<?> searchReturnByPage(@RequestBody Map<String, Object> condition);

    @Operation(summary = "审批退货（管理端）")
    @PostMapping("/v1/trade/return/approve")
    RowsDTO approveReturn(@RequestBody Object entity);

    @Operation(summary = "拒绝退货（管理端）")
    @PostMapping("/v1/trade/return/reject")
    RowsDTO rejectReturn(@RequestBody Object entity);

    @Operation(summary = "查询退货详情（管理端）")
    @GetMapping("/v1/trade/return/findById")
    Object findReturnById(@RequestParam("id") Long id);
}
