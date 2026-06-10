package cn.net.mall.message.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

public class AuthHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Object usernameObj = attributes.get("username");
        String name = usernameObj instanceof String s && s.length() > 0 ? s : "guest-" + UUID.randomUUID();
        return () -> name;
    }
}
