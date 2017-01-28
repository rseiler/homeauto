package at.rseiler.homeauto.milightweb.websocket;

import at.rseiler.homeauto.common.milight.MiLightWiFiBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configuration for the WebSocket integration.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MiLightWiFiBoxService miLightWiFiBoxService;

    @Autowired
    public WebSocketConfig(MiLightWiFiBoxService miLightWiFiBoxService) {
        this.miLightWiFiBoxService = miLightWiFiBoxService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(infoWebSocketHandler(), "/ws");
    }

    @Bean
    public WebSocketHandler infoWebSocketHandler() {
        return new StreamWebSocketHandler(miLightWiFiBoxService);
    }
}
