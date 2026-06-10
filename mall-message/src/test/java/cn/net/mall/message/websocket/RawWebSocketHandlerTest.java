package cn.net.mall.message.websocket;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RawWebSocketHandlerTest {

    @Test
    public void should_reply_pong_when_receive_ping() throws Exception {
        RawWebSocketHandler handler = new RawWebSocketHandler();
        WebSocketSession session = mock(WebSocketSession.class);

        handler.handleTextMessage(session, new TextMessage("ping"));

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(captor.capture());
        assertEquals("pong", captor.getValue().getPayload());
    }

    @Test
    public void should_echo_message_when_receive_text() throws Exception {
        RawWebSocketHandler handler = new RawWebSocketHandler();
        WebSocketSession session = mock(WebSocketSession.class);

        handler.handleTextMessage(session, new TextMessage("hello"));

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(captor.capture());
        assertEquals("hello", captor.getValue().getPayload());
    }
}
