package at.rseiler.homeauto.common.watcher;

import at.rseiler.homeauto.common.network.IpFromTo;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class IpFromToTest {

    @Test
    public void testSingleIp() {
        IpFromTo ipFromTo = new IpFromTo("127.0.0.1", "127.0.0.1");
        List<String> ips = Lists.newArrayList(ipFromTo.iterator());

        assertThat(ips, is(Collections.singletonList("127.0.0.1")));
    }

    @Test
    public void testOneRangePart() {
        IpFromTo ipFromTo = new IpFromTo("127.0.0.1", "127.0.0.2");
        List<String> ips = Lists.newArrayList(ipFromTo.iterator());

        assertThat(ips, is(Arrays.asList("127.0.0.1", "127.0.0.2")));
    }

    @Test
    public void testTwoRangeParts() {
        IpFromTo ipFromTo = new IpFromTo("127.0.0.1", "127.0.1.2");
        List<String> ips = Lists.newArrayList(ipFromTo.iterator());

        assertThat(ips, is(Arrays.asList("127.0.0.1", "127.0.0.2", "127.0.1.1", "127.0.1.2")));
    }

    @Test
    public void testThreeRangeParts() {
        IpFromTo ipFromTo = new IpFromTo("127.0.0.1", "127.1.1.2");
        List<String> ips = Lists.newArrayList(ipFromTo.iterator());

        assertThat(ips, is(Arrays.asList("127.0.0.1", "127.0.0.2", "127.0.1.1", "127.0.1.2", "127.1.0.1", "127.1.0.2",
                "127.1.1.1", "127.1.1.2")));
    }

    @Test
    public void testForRangeParts() {
        IpFromTo ipFromTo = new IpFromTo("127.0.0.1", "128.1.1.2");
        List<String> ips = Lists.newArrayList(ipFromTo.iterator());

        assertThat(ips, is(Arrays.asList("127.0.0.1", "127.0.0.2", "127.0.1.1", "127.0.1.2", "127.1.0.1", "127.1.0.2",
                "127.1.1.1", "127.1.1.2", "128.0.0.1", "128.0.0.2", "128.0.1.1", "128.0.1.2", "128.1.0.1", "128.1.0.2",
                "128.1.1.1", "128.1.1.2"
        )));
    }
}