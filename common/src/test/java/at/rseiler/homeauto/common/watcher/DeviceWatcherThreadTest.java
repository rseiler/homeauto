package at.rseiler.homeauto.common.watcher;

import at.rseiler.homeauto.common.network.IpFromTo;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DeviceWatcherThreadTest {
    @Test
    public void testCalcThreadCountWith1Ip() throws Exception {
        int threadCount = DeviceWatcherThread.calcThreadCount(new IpFromTo("192.168.0.1", "192.168.0.1"));

        assertThat(threadCount, is(1));
    }
    
    @Test
    public void testCalcThreadCountWith9Ips() throws Exception {
        int threadCount = DeviceWatcherThread.calcThreadCount(new IpFromTo("192.168.0.12", "192.168.0.20"));

        assertThat(threadCount, is(3));
    }

    @Test
    public void testCalcThreadCountWithMaxIps() throws Exception {
        int threadCount = DeviceWatcherThread.calcThreadCount(new IpFromTo("192.168.0.12", "192.168.0.255"));

        assertThat(threadCount, is(DeviceWatcherThread.MAX_THREAD_COUNT));
    }
}