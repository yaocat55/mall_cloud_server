package cn.net.mall.pay.integration;

import cn.net.mall.order.dto.OrderDTO;
import com.alibaba.fastjson.JSONObject;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 支付宝支付
 */
@Component
public class AliPayIntegration {
    @Autowired
    private Config config;

    /**
     * 调用支付宝预下订单接口
     *
     * @param orderDTO 订单实体
     * @return 二维码url
     * @throws Exception
     */
    public String pay(OrderDTO orderDTO) throws Exception {
        Factory.setOptions(config);
        //调用支付宝的接口
        AlipayTradePrecreateResponse payResponse = Factory.Payment.FaceToFace()
                .preCreate("",
                        orderDTO.getCode(),
                        orderDTO.getPayAmount().toString());
        //参照官方文档响应示例，解析返回结果
        String httpBodyStr = payResponse.getHttpBody();
        JSONObject jsonObject = JSONObject.parseObject(httpBodyStr);
        return jsonObject.getJSONObject("alipay_trade_precreate_response").get("qr_code").toString();
    }
}
