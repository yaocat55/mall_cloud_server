package cn.net.mall.config;

import cn.net.mall.decoder.FeignResultDecoder;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
@ConditionalOnClass(name = "feign.Feign")
public class FeignDecoderConfig {

    private final ObjectFactory<HttpMessageConverters> messageConverters;

    public FeignDecoderConfig(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Bean
    public Decoder feignDecoder() {
        return new FeignResultDecoder(new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(this.messageConverters))));
    }
}
