package at.rseiler.homeauto.wirelesssocket;

import at.rseiler.homeauto.common.YmlUtil;
import at.rseiler.homeauto.common.watcher.DeviceWatcher;
import at.rseiler.homeauto.common.watcher.config.DeviceWatcherConfig;
import at.rseiler.homeauto.wirelesssocket.config.WirelessSocketConfigWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Entry point for the Wireless-Socket application.
 * <p>
 * It reads the from the working directory the wireless-socket.yml config file and starts the application.
 */
public final class WirelessSocketMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(WirelessSocketMain.class);

    public static void main(String[] args) {
        try {
            File yml = new File("wireless-socket.yml");
            WirelessSocketConfigWrapper wirelessSocketConfigWrapper = YmlUtil.read(yml, WirelessSocketConfigWrapper.class);
            DeviceWatcherConfig deviceWatcherConfig = wirelessSocketConfigWrapper.getDeviceWatcher();
            DeviceWatcher deviceWatcher = new DeviceWatcher(deviceWatcherConfig);
            WirelessSocketApp wirelessSocketApp = new WirelessSocketApp(wirelessSocketConfigWrapper.getWirelessSocket(), deviceWatcher);
            wirelessSocketApp.start();
            deviceWatcher.start();
        } catch (IOException e) {
            LOGGER.error("Failed to read wireless-socket.yml", e);
        }
    }

    private WirelessSocketMain() {
    }
}
