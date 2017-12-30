package at.rseiler.homeauto.milightweb;

import at.rseiler.homeauto.common.milight.MiLightWiFiBoxService;
import at.rseiler.homeauto.common.milight.MiLightWiFiBoxServiceBuilder;
import at.rseiler.homeauto.common.milight.config.MiLightWiFiBoxConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

/**
 * The MiLight-Web-Application.
 * <p>
 * Connects to a MiLight-Wifi-Box and provides a website to control the MiLights.
 */
@SpringBootApplication
public class MiLightWebApp {

    public static void main(String[] args) {
        SpringApplication.run(MiLightWebApp.class, args);
    }

    @Bean
    MiLightWiFiBoxService miLightWiFiBoxService(
            @Value("${miLight.wifiBox.ipFrom}") String ipFrom,
            @Value("${miLight.wifiBox.ipTo}") String ipTo,
            @Value("${miLight.wifiBox.port:80}") int port) {
        MiLightWiFiBoxConfig config = new MiLightWiFiBoxConfig(ipFrom, ipTo, port, Collections.emptyList());
        return new MiLightWiFiBoxServiceBuilder(config)
                .build()
                .orElseThrow(() -> new RuntimeException("Failed to connect to wifi box"));
    }
}
