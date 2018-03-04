package at.rseiler.homeauto.milight;

import at.rseiler.homeauto.common.YmlUtil;
import at.rseiler.homeauto.common.milight.MiLightWiFiBoxService;
import at.rseiler.homeauto.common.milight.MiLightWiFiBoxServiceBuilder;
import at.rseiler.homeauto.common.watcher.DeviceWatcher;
import at.rseiler.homeauto.common.watcher.config.DeviceWatcherConfig;
import at.rseiler.homeauto.milight.config.MiLightConfigWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Entry point for the MiLight application.
 * <p>
 * It reads the from the working directory the milight.yml config file and starts the application.
 */
public final class MiLightMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(MiLightMain.class);

    public static void main(String[] args) {
        try {
            File yml = new File("milight.yml");
            MiLightConfigWrapper config = YmlUtil.read(yml, MiLightConfigWrapper.class);
            DeviceWatcherConfig deviceWatcherConfig = config.getDeviceWatcher();
            DeviceWatcher deviceWatcher = new DeviceWatcher(deviceWatcherConfig);
            MiLightWiFiBoxService miLightWiFiBoxService = new MiLightWiFiBoxServiceBuilder(config.getMiLight().getWifiBox()).build().orElseThrow(() -> new RuntimeException("Failed to connect to the milight wifi box"));
            MiLightApp miLightApp = new MiLightApp(config.getMiLight(), null, deviceWatcher, miLightWiFiBoxService);
            miLightApp.start();
            deviceWatcher.start();
        } catch (IOException e) {
            LOGGER.error("Failed to read milight.yml", e);
        }
    }

    private MiLightMain() {
    }
}
