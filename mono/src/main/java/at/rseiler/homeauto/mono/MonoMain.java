package at.rseiler.homeauto.mono;

import at.rseiler.homeauto.common.YmlUtil;
import at.rseiler.homeauto.common.milight.MiLightWiFiBoxServiceBuilder;
import at.rseiler.homeauto.common.watcher.DeviceWatcher;
import at.rseiler.homeauto.common.watcher.config.DeviceWatcherConfig;
import at.rseiler.homeauto.milight.MiLightApp;
import at.rseiler.homeauto.mono.config.MonoConfigWrapper;
import at.rseiler.homeauto.wirelesssocket.WirelessSocketApp;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public final class MonoMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonoMain.class);

    public static void main(String[] args) {
        try {
            File yml = new File("mono.yml");
            MonoConfigWrapper config = YmlUtil.read(yml, MonoConfigWrapper.class);
            LOGGER.trace(Files.toString(yml, StandardCharsets.UTF_8));
            DeviceWatcherConfig deviceWatcherConfig = config.getDeviceWatcher();
            DeviceWatcher deviceWatcher = new DeviceWatcher(deviceWatcherConfig);

            WirelessSocketApp wirelessSocketApp = new WirelessSocketApp(config.getWirelessSocket(), deviceWatcher);
            wirelessSocketApp.start();

            MiLightWiFiBoxServiceBuilder builder = new MiLightWiFiBoxServiceBuilder(config.getMiLight().getWifiBox());
            MiLightApp miLightApp = new MiLightApp(config.getMiLight(), deviceWatcher, builder);
            miLightApp.start();

            deviceWatcher.start();
        } catch (IOException e) {
            LOGGER.error("Failed to read wireless-socket.yml", e);
        }
    }

    private MonoMain() {
    }
}
