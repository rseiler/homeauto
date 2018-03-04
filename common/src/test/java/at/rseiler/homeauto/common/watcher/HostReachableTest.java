package at.rseiler.homeauto.common.watcher;

import at.rseiler.homeauto.common.network.HostReachable;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class HostReachableTest {
    @Test
    public void testLocalhost() throws Exception {
        assertTrue(new HostReachable("127.0.0.1").call());
    }
}