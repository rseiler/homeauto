package at.rseiler.homeauto.wirelesssocket;

import at.rseiler.homeauto.common.YmlUtil;
import at.rseiler.homeauto.common.fortest.FileUtil;
import at.rseiler.homeauto.common.watcher.DeviceWatcher.DeviceEvent;
import at.rseiler.homeauto.common.watcher.DeviceWatcher.State;
import at.rseiler.homeauto.common.watcher.ForTestDeviceWatcher;
import at.rseiler.homeauto.wirelesssocket.config.WirelessSocketConfigWrapper;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class WirelessSocketAppTest {

    private WirelessSocketApp wirelessSocketApp;
    private ForTestDeviceWatcher deviceWatcher;
    private int sendCount;

    @Before
    public void setUp() throws Exception {
        File yml = FileUtil.get("wireless-socket","src/test/resources/wireless-socket.yml");
        WirelessSocketConfigWrapper config = YmlUtil.read(yml, WirelessSocketConfigWrapper.class);
        sendCount = config.getWirelessSocket().getSendCount();
        deviceWatcher = new ForTestDeviceWatcher(null);
        wirelessSocketApp = spy(new WirelessSocketApp(config.getWirelessSocket(), deviceWatcher));
        doNothing().when(wirelessSocketApp).send(any());
        wirelessSocketApp.start();
    }

    @Test
    public void testSendOn() throws Exception {
        deviceWatcher.fireEvent(new DeviceEvent("127.0.0.1", "a1-23-bc-4d-56-e7", State.ON));

        verify(wirelessSocketApp, times(sendCount)).send(State.ON);
    }

    @Test
    public void testSendOff() throws Exception {
        deviceWatcher.fireEvent(new DeviceEvent("127.0.0.1", "a1-23-bc-4d-56-e7", State.OFF));

        verify(wirelessSocketApp, times(sendCount)).send(State.OFF);
    }
}