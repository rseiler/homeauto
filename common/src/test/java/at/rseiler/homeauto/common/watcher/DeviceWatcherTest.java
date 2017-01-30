package at.rseiler.homeauto.common.watcher;

import at.rseiler.homeauto.common.YmlUtil;
import at.rseiler.homeauto.common.fortest.FileUtil;
import at.rseiler.homeauto.common.network.ArpCommand;
import at.rseiler.homeauto.common.network.ArpCommand.ArpCmdSupplier;
import at.rseiler.homeauto.common.network.HostReachable;
import at.rseiler.homeauto.common.network.HostReachable.HostReachableSupplier;
import at.rseiler.homeauto.common.watcher.DeviceWatcher.DeviceEvent;
import at.rseiler.homeauto.common.watcher.DeviceWatcher.State;
import at.rseiler.homeauto.common.watcher.config.DeviceWatcherConfig;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

public class DeviceWatcherTest {

    private static final String IP = "127.0.0.1";
    private static final String MAC = "a1-23-bc-4d-56-e7";
    private DeviceWatcherConfigWrapper config;
    private ArpCommand arpCommand;
    private Consumer<DeviceEvent> deviceEventConsumer;
    private HostReachable hostReachable;
    private DeviceWatcher deviceWatcher;

    @Before
    public void setUp() throws IOException {
        File yml = FileUtil.get("common", "src/test/resources/device-watcher.yml");
        config = YmlUtil.read(yml, DeviceWatcherConfigWrapper.class);
        hostReachable = mock(HostReachable.class);
        arpCommand = mock(ArpCommand.class);
        when(arpCommand.getMacAddress(IP)).thenReturn(MAC);
        deviceEventConsumer = spy(new DeviceEventConsumer());
    }

    @Test(timeout = 2000)
    public void testOnline() throws Exception {
        when(hostReachable.call()).thenReturn(false, true);

        createDeviceWatcher();

        verify(deviceEventConsumer, timeout(1000).times(1)).accept(eq(createDeviceEvent(State.ON)));
        deviceWatcher.stop();
    }

    @Test(timeout = 2000)
    public void testOffline() throws Exception {
        when(hostReachable.call()).thenReturn(true, false);

        createDeviceWatcher();

        verify(deviceEventConsumer, timeout(1000).times(1)).accept(eq(createDeviceEvent(State.OFF)));
        deviceWatcher.stop();
    }

    private void createDeviceWatcher() {
        HostReachableSupplier hostReachableSupplier = mock(HostReachableSupplier.class);
        when(hostReachableSupplier.create(anyString())).thenReturn(hostReachable);
        ArpCmdSupplier arpCmdSupplier = mock(ArpCmdSupplier.class);
        when(arpCmdSupplier.create()).thenReturn(arpCommand);
        deviceWatcher = new DeviceWatcher(config.getDeviceWatcher(), hostReachableSupplier, arpCmdSupplier);
        deviceWatcher.subscribe(deviceEventConsumer);
        deviceWatcher.start();
    }

    private DeviceEvent createDeviceEvent(State on) {
        return new DeviceEvent(IP, MAC, on);
    }

    @Value
    @Builder
    @JsonDeserialize(builder = DeviceWatcherConfigWrapper.DeviceWatcherConfigWrapperBuilder.class)
    static class DeviceWatcherConfigWrapper {
        private final DeviceWatcherConfig deviceWatcher;

        @JsonPOJOBuilder(withPrefix = "")
        static final class DeviceWatcherConfigWrapperBuilder {
        }
    }

    /**
     * This class is needed because {@link org.mockito.Mockito#spy(Object)} doesn't work on lambdas.
     */
    private static class DeviceEventConsumer implements Consumer<DeviceEvent> {
        @Override
        public void accept(DeviceEvent deviceEvent) {
        }
    }
}