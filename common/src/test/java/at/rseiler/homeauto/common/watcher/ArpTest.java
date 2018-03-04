package at.rseiler.homeauto.common.watcher;

import at.rseiler.homeauto.common.network.ArpCommand;
import at.rseiler.homeauto.common.network.ArpCommand.ArpCmdSupplier;
import org.junit.Test;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ArpTest {
    @Test
    public void testWindows() {
        ArpCommand arp = new ArpCommand("  192.168.0.1          a1-23-bc-4d-56-e7     dynamic");
        String macAddress = arp.getMacAddress("192.168.0.1");

        assertThat(macAddress, is("a1-23-bc-4d-56-e7"));
    }

    @Test
    public void testUnix() {
        ArpCommand arp = new ArpCommand("  ? (192.168.0.1) at a1:23:bc:4d:56:e7 [ether] on eth0");
        String macAddress = arp.getMacAddress("192.168.0.1");

        assertThat(macAddress, is("a1:23:bc:4d:56:e7"));
    }

    @Test
    public void testBuilder() {
        assertThat(new ArpCmdSupplier().create().getArp().length(), greaterThan(0));
    }
}