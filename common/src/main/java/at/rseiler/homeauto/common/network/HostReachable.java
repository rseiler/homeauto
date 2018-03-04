package at.rseiler.homeauto.common.network;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Callable;

/**
 * Checks if the host is reachable.
 */
@Data
public class HostReachable implements Callable<Boolean> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HostReachable.class);
    private static final int TIMEOUT = 7500;
    private final String host;

    /**
     * Checks if the host is reachable.
     *
     * @return true if the host is reachable
     */
    @Override
    public Boolean call() {
        try {
            return InetAddress.getByName(host).isReachable(TIMEOUT);
        } catch (IOException e) {
            LOGGER.error("Failed to determinate if host is reachable", e);
        }

        return false;
    }

    /**
     * Supplier for the HostReachable.
     */
    public static class HostReachableSupplier {

        /**
         * Creates a HostReachable for the specified host.
         *
         * @param host the IP of the host
         * @return the HostReachable
         */
        public HostReachable create(String host) {
            return new HostReachable(host);
        }
    }
}
