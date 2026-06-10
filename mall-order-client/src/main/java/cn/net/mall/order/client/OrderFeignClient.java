package cn.net.mall.order.client;

import cn.net.mall.entity.ResponsePageEntity;
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
import cn.net.mall.order.dto.OrderEvaluateDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static cn.net.mall.order.constant.AppConstant.ORDER_SERVICE_NAME;

@FeignClient(value = ORDER_SERVICE_NAME, contextId = "orderFeignClient")
public interface OrderFeignClient {

    @PostMapping("/v1/mobile/trade/create")
    Long create(@RequestBody OrderDTO orderDTO);

    @GetMapping("/v1/mobile/trade/getDetail/{id}")
    OrderDTO findById(@PathVariable("id") Long id);

    @PostMapping("/v1/mobile/trade/search")
    ResponsePageEntity<OrderDTO> search(@RequestBody OrderConditionDTO orderConditionDTO);

    @PostMapping("/v1/mobile/trade/update")
    int update(@RequestBody OrderDTO orderDTO);

    @PostMapping("/v1/mobile/trade/delete")
    int delete(@RequestBody List<Long> ids);

    @PostMapping("/v1/mobile/trade/confirm")
    OrderConfirmRespDTO confirm(@RequestBody OrderConfirmReqDTO req);

    @PostMapping("/v1/mobile/trade/submit")
    OrderSubmitRespDTO submit(@RequestBody OrderSubmitDTO submitDTO);

    @GetMapping("/v1/mobile/trade/getUserOrderCount")
    OrderTradeCountDTO getUserOrderCount();

    @PostMapping("/v1/mobile/trade/cancel")
    void cancel(@RequestBody OrderOperateDTO operateDTO);

    @PostMapping("/v1/mobile/trade/confirmReceive")
    void confirmReceive(@RequestBody OrderOperateDTO operateDTO);

    @PostMapping("/v1/mobile/trade/evaluate")
    void evaluate(@RequestBody OrderEvaluateDTO evaluateDTO);

    @PostMapping("/v1/mobile/trade/getTradeItem")
    TradeItemDTO getTradeItem(@RequestBody TradeItemReqDTO req);

    @GetMapping("/v1/mobile/trade/getUserOrderTradeCount")
    OrderTradeCountDTO getUserOrderTradeCount();

    @GetMapping("/v1/mobile/trade/getDetailByCode/{code}")
    TradeDetailDTO getDetailByCode(@PathVariable("code") String code);

    @GetMapping("/v1/mobile/trade/getTrade/{code}")
    OrderDTO getTrade(@PathVariable("code") String code);

    @PostMapping("/v1/mobile/trade/return/apply")
    Long applyReturn(@RequestBody OrderReturnApplyDTO dto);

    @PostMapping("/v1/mobile/trade/return/search")
    ResponsePageEntity<OrderReturnApplyDTO> searchReturn(@RequestBody OrderReturnConditionDTO condition);
}
