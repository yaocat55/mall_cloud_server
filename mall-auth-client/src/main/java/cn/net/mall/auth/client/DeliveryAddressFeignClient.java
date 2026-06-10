package cn.net.mall.auth.client;

import cn.net.mall.auth.dto.DeliveryAddressDTO;
import cn.net.mall.auth.dto.DeliveryAddressDefaultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.net.mall.auth.constant.AppConstant.AUTH_SERVICE_NAME;

@FeignClient(value = AUTH_SERVICE_NAME, contextId = "deliveryAddressFeignClient")
public interface DeliveryAddressFeignClient {

    /**
     * 设置默认收货地址
     *
     * @param deliveryAddressDefaultDTO 收货地址实体
     */
    @PostMapping("/v1/mobile/deliveryAddress/setDefaultDeliveryAddress")
    void setDefaultDeliveryAddress(@RequestBody DeliveryAddressDefaultDTO deliveryAddressDefaultDTO);

    /**
     * 获取某用户收货地址列表
     *
     * @return 收货地址列表
     */
    @GetMapping("/v1/mobile/deliveryAddress/getUserDeliveryAddressList")
    List<DeliveryAddressDTO> getUserDeliveryAddressList();

    /**
     * 获取收货地址详情
     *
     * @return 收货地址详情
     */
    @GetMapping("/v1/mobile/deliveryAddress/getDetail")
    DeliveryAddressDTO getDetail(@RequestParam("id") Long id);

    /**
     * 批量删除收货地址
     *
     * @param ids 收货地址ID集合
     * @return 影响行数
     */
    @PostMapping("/v1/mobile/deliveryAddress/deleteByIds")
    int deleteByIds(@RequestBody List<Long> ids);

    /**
     * 保存收货地址
     *
     * @param deliveryAddressDTO 收货地址实体
     */
    @PostMapping("/v1/mobile/deliveryAddress/save")
    void save(@RequestBody DeliveryAddressDTO deliveryAddressDTO);
}
