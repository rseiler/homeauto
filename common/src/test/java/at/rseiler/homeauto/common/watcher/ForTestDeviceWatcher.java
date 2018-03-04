package at.rseiler.homeauto.common.watcher;

import at.rseiler.homeauto.common.watcher.config.DeviceWatcherConfig;


public class ForTestDeviceWatcher extends DeviceWatcher {
    public ForTestDeviceWatcher(DeviceWatcherConfig deviceWatcherConfig) {
        super(deviceWatcherConfig);
    }

    @Override
    public void fireEvent(DeviceEvent deviceEvent) {
        super.fireEvent(deviceEvent);
    }
}
