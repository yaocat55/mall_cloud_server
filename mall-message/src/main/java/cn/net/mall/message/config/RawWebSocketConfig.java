package cn.net.mall.message.config;

import cn.net.mall.message.websocket.AuthHandshakeInterceptor;
import cn.net.mall.message.websocket.RawWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class RawWebSocketConfig implements WebSocketConfigurer {

    private final RawWebSocketHandler rawWebSocketHandler;
    private final AuthHandshakeInterceptor authHandshakeInterceptor;

    public RawWebSocketConfig(RawWebSocketHandler rawWebSocketHandler,
                              AuthHandshakeInterceptor authHandshakeInterceptor) {
        this.rawWebSocketHandler = rawWebSocketHandler;
        this.authHandshakeInterceptor = authHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(rawWebSocketHandler, "/ws")
                .addInterceptors(authHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }
}
