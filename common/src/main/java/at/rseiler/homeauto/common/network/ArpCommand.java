package at.rseiler.homeauto.common.network;

import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holds the result of the "arp -a" command and returns the mac address for a specific IP.
 */
@Data
public class ArpCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArpCommand.class);
    private final String arp;

    /**
     * If the IP exists then it returns the mac address.
     *
     * @param host the IP of the host
     * @return the mac address or an empty string
     */
    public String getMacAddress(String host) {
        Matcher matcher = Pattern.compile(host + ".*([\\d\\w:-]{17})").matcher(arp);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    /**
     * Returns the "arp -a" command result.
     *
     * @return the arp
     */
    public String getArp() {
        return arp;
    }

    /**
     * Supplier for the ArpCommand.
     */
    public static class ArpCmdSupplier {

        /**
         * Creates a new ArpCommand.
         *
         * @return the ArpCommand
         */
        public ArpCommand create() {
            try {
                Process process = new ProcessBuilder("arp", "-a").start();
                return new ArpCommand(IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                LOGGER.error("Failed to execute: arp -a", e);
            }

            return new ArpCommand("");
        }
    }
}
