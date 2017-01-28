package at.rseiler.homeauto.common.watcher;

import at.rseiler.homeauto.common.network.ArpCommand;
import at.rseiler.homeauto.common.network.ArpCommand.ArpCmdSupplier;
import at.rseiler.homeauto.common.network.HostReachable.HostReachableSupplier;
import at.rseiler.homeauto.common.network.IpFromTo;
import at.rseiler.homeauto.common.watcher.DeviceWatcher.DeviceEvent;
import at.rseiler.homeauto.common.watcher.config.DeviceWatcherConfig;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RequiredArgsConstructor
class DeviceWatcherThread extends Thread {
    static final int MAX_THREAD_COUNT = 9;
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceWatcherThread.class);
    private static final int THREAD_FACTOR = 3;
    private final DeviceWatcher deviceWatcher;
    private final DeviceWatcherConfig deviceWatcherConfig;
    private final HostReachableSupplier hostReachableSupplier;
    private final ArpCmdSupplier arpCmdSupplier;
    private final Map<String, DeviceData> map = new HashMap<>();
    private boolean running = true;
    private boolean firstRun = true;

    @Override
    public void run() {
        IpFromTo ipFromTo = new IpFromTo(deviceWatcherConfig.getIpFrom(), deviceWatcherConfig.getIpTo());
        int threadCount = calcThreadCount(ipFromTo);
        LOGGER.info("Using {} thread{}", threadCount, threadCount > 1 ? "s" : "");
        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);

        while (running) {
            List<String> reachableHosts = new ArrayList<>();

            for (String ip : ipFromTo) {
                if (!map.containsKey(ip) || map.get(ip).time < System.currentTimeMillis() - deviceWatcherConfig.getTtl()) {
                    Future<Boolean> reachableFuture = threadPool.submit(hostReachableSupplier.create(ip));

                    try {
                        if (reachableFuture.get()) {
                            reachableHosts.add(ip);
                        } else if (map.containsKey(ip)) {
                            handleUnreachableHost(ip);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        LOGGER.debug("Interrupted", e);
                    }
                }
            }

            handleReachableHosts(reachableHosts);
            firstRun = false;

            try {
                Thread.sleep(deviceWatcherConfig.getSleepTime());
            } catch (InterruptedException e) {
                LOGGER.debug("Interrupted", e);
            }
        }
    }

    void shutdown() {
        running = false;
        interrupt();
    }

    private void handleUnreachableHost(String host) {
        DeviceData deviceData = map.remove(host);
        deviceWatcher.fireEvent(new DeviceEvent(host, deviceData.mac, DeviceWatcher.State.OFF));
    }

    private void handleReachableHosts(List<String> reachableHosts) {
        if (!reachableHosts.isEmpty()) {
            ArpCommand arpCommand = arpCmdSupplier.create();

            for (String host : reachableHosts) {
                String macAddress = arpCommand.getMacAddress(host);

                if (!firstRun && !map.containsKey(host) && deviceWatcherConfig.getMacAddress().contains(macAddress)) {
                    deviceWatcher.fireEvent(new DeviceEvent(host, macAddress, DeviceWatcher.State.ON));
                }

                map.put(host, new DeviceData(macAddress, System.currentTimeMillis()));
            }
        }
    }

    static int calcThreadCount(IpFromTo ipFromTo) {
        int count = 2;

        for (Iterator<String> it = ipFromTo.iterator(); count < MAX_THREAD_COUNT * THREAD_FACTOR && it.hasNext(); count++) {
            it.next();
        }

        return count / THREAD_FACTOR;
    }

    @Data
    private static final class DeviceData {
        private final String mac;
        private final long time;
    }
}
