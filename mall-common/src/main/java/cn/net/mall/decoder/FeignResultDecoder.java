package cn.net.mall.decoder;

import cn.hutool.json.JSONUtil;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.util.ApiResult;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@Slf4j
public class FeignResultDecoder implements Decoder {

    private final Decoder delegate;

    public FeignResultDecoder(Decoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = response.body().asInputStream().read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        byte[] body = byteArrayOutputStream.toByteArray();
        String bodyStr = new String(body, StandardCharsets.UTF_8);
        log.info("调用第三方接口返回:{}", bodyStr);
//        if (bodyStr != null) {
//            if (bodyStr.contains("message")) {
//                ApiResult apiResult = JSONUtil.toBean(bodyStr, ApiResult.class);
//                return apiResult;
//            }
//        }
        return delegate.decode(response.toBuilder().body(bodyStr, StandardCharsets.UTF_8).build(), type);
    }
}
