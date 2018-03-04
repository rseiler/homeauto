package at.rseiler.homeauto.common.watcher;

import at.rseiler.homeauto.common.network.ArpCommand.ArpCmdSupplier;
import at.rseiler.homeauto.common.network.HostReachable.HostReachableSupplier;
import at.rseiler.homeauto.common.watcher.config.DeviceWatcherConfig;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Checks periodically if the one of the searched IPs is reachable and matches a searched mac address. It stores which
 * devices are online and if the state of an device changes (goes online or offline) then the observers are notified.
 */
public class DeviceWatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceWatcher.class);
    private final EventBus eventBus = new EventBus();
    private final DeviceWatcherThread deviceWatcherThread;

    public DeviceWatcher(DeviceWatcherConfig deviceWatcherConfig) {
        this(deviceWatcherConfig, new HostReachableSupplier(), new ArpCmdSupplier());
    }

    public DeviceWatcher(DeviceWatcherConfig deviceWatcherConfig, HostReachableSupplier hostReachableSupplier, ArpCmdSupplier arpCmdSupplier) {
        deviceWatcherThread = new DeviceWatcherThread(this, deviceWatcherConfig, hostReachableSupplier, arpCmdSupplier);
    }

    public void subscribe(Consumer<DeviceEvent> consumer) {
        eventBus.register(new DeviceEventSubscriber(consumer));
    }

    public void start() {
        deviceWatcherThread.start();
    }

    public void stop() {
        deviceWatcherThread.shutdown();
    }

    void fireEvent(DeviceEvent deviceEvent) {
        LOGGER.debug("DeviceEvent: {}", deviceEvent);
        eventBus.post(deviceEvent);
    }

    public enum State {
        ON, OFF
    }

    @Data
    public static class DeviceEvent {
        private final String ip;
        private final String mac;
        private final State state;
    }

    @RequiredArgsConstructor
    private static class DeviceEventSubscriber {
        private final Consumer<DeviceEvent> consumer;

        @Subscribe
        public void deviceEvent(DeviceEvent deviceEvent) {
            consumer.accept(deviceEvent);
        }
    }
}
