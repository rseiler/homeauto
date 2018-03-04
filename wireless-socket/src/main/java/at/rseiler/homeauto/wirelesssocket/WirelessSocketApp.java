package at.rseiler.homeauto.wirelesssocket;

import at.rseiler.homeauto.common.watcher.DeviceWatcher;
import at.rseiler.homeauto.common.watcher.DeviceWatcher.DeviceEvent;
import at.rseiler.homeauto.common.watcher.DeviceWatcher.State;
import at.rseiler.homeauto.wirelesssocket.config.WirelessSocketConfig;
import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Scans if a specific device is turned on/off and turns on/off the wireless socket.
 */
@RequiredArgsConstructor
public class WirelessSocketApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(WirelessSocketApp.class);
    private final WirelessSocketConfig config;
    private final DeviceWatcher deviceWatcher;

    /**
     * Starts listening to the {@link WirelessSocketApp#deviceWatcher}'s events.
     */
    public void start() {
        LOGGER.info("Wireless Socket: started");
        deviceWatcher.subscribe(this::observe);
    }

    private void observe(DeviceEvent deviceEvent) {
        if (config.getMacAddress().contains(deviceEvent.getMac())) {
            try {
                for (int i = 0; i < config.getSendCount(); i++) {
                    send(deviceEvent.getState());
                    Thread.sleep(config.getSendSleep());
                }
            } catch (InterruptedException e) {
                LOGGER.trace("Interrupted", e);
            }
        }
    }

    @VisibleForTesting
    void send(State state) {
        try {
            LOGGER.info("Send {}", state);
            String param = state == State.ON ? "1" : "0";
            Runtime.getRuntime().exec(new String[]{"sh", "-c", config.getCommand() + param});
        } catch (IOException e) {
            LOGGER.error("Failed to turn on/off the light", e);
        }
    }
}
