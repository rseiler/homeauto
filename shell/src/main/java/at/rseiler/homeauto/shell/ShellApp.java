package at.rseiler.homeauto.shell;

import at.rseiler.homeauto.common.milight.MiLightWiFiBoxService;
import at.rseiler.homeauto.common.milight.MiLightWiFiBoxServiceBuilder;
import at.rseiler.homeauto.common.milight.config.MiLightWiFiBoxConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

@SpringBootApplication
public class ShellApp {
    public static void main(String[] args) {
        SpringApplication.run(ShellApp.class, args);
    }

    @Bean
    public MiLightWiFiBoxService miLightWiFiBoxService() {
        return new MiLightWiFiBoxServiceBuilder(new MiLightWiFiBoxConfig(
                "192.168.0.12",
                "192.168.0.21",
                80,
                Collections.emptyList()))
                .build()
                .get();
    }
}
