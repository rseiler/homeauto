package at.rseiler.homeauto.milightweb.websocket;

import at.rseiler.homeauto.common.milight.MiLightCommand;
import at.rseiler.homeauto.common.milight.MiLightWiFiBoxService;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Listens to the WebSocket messages and passes them to {@link MiLightWiFiBoxService#exec(MiLightCommand)}.
 */
class StreamWebSocketHandler extends TextWebSocketHandler {

    private final MiLightWiFiBoxService miLightWiFiBoxService;

    StreamWebSocketHandler(MiLightWiFiBoxService miLightWiFiBoxService) {
        this.miLightWiFiBoxService = miLightWiFiBoxService;
    }

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    protected synchronized void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        handleCommand(message.getPayload());
    }

    private synchronized void handleCommand(String command)  {
        miLightWiFiBoxService.exec(MiLightCommand.fromString(command));
    }
}