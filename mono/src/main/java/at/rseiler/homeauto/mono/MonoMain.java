package at.rseiler.homeauto.mono;

import at.rseiler.homeauto.arduino.Arduino;
import at.rseiler.homeauto.common.YmlUtil;
import at.rseiler.homeauto.common.milight.MiLightWiFiBoxService;
import at.rseiler.homeauto.common.watcher.DeviceWatcher;
import at.rseiler.homeauto.common.watcher.config.DeviceWatcherConfig;
import at.rseiler.homeauto.display.DisplayApp;
import at.rseiler.homeauto.milight.MiLightApp;
import at.rseiler.homeauto.milightweb.MiLightWebApp;
import at.rseiler.homeauto.mono.config.MonoConfigWrapper;
import at.rseiler.homeauto.wirelesssocket.WirelessSocketApp;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Entry point for the Mono application.
 * <p>
 * It reads the from the working directory the mono.yml config file and starts the applications:
 * <p>
 * <ul>
 * <li>MiLight</li>
 * <li>Wireless Socket</li>
 * </ul>
 */

@SpringBootApplication
@Import({MiLightWebApp.class, DisplayApp.class})
public class MonoMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonoMain.class);

    public static void main(String[] args) {
        SpringApplication.run(MonoMain.class, args);
    }

    @Configuration
    public static class Init {
        private final MiLightWiFiBoxService miLightWiFiBoxService;

        @Autowired
        public Init(MiLightWiFiBoxService miLightWiFiBoxService) {
            this.miLightWiFiBoxService = miLightWiFiBoxService;
        }

        @PostConstruct
        public void init() throws IOException {
            try {
                File yml = new File("mono.yml");
                MonoConfigWrapper config = YmlUtil.read(yml, MonoConfigWrapper.class);
                LOGGER.trace(Files.toString(yml, StandardCharsets.UTF_8));
                Arduino arduino = new Arduino(config.getArduino());
                DeviceWatcherConfig deviceWatcherConfig = config.getDeviceWatcher();
                DeviceWatcher deviceWatcher = new DeviceWatcher(deviceWatcherConfig);

                WirelessSocketApp wirelessSocketApp = new WirelessSocketApp(config.getWirelessSocket(), deviceWatcher);
                wirelessSocketApp.start();

                MiLightApp miLightApp = new MiLightApp(config.getMiLight(), arduino, deviceWatcher, miLightWiFiBoxService);
                miLightApp.start();

                arduino.start();
                deviceWatcher.start();
            } catch (IOException e) {
                LOGGER.error("Failed to read mono.yml", e);
                throw e;
            }
        }
    }
}
