package at.rseiler.homeauto.wirelesssocket.config;

import at.rseiler.homeauto.common.watcher.config.DeviceWatcherConfig;
import at.rseiler.homeauto.wirelesssocket.config.WirelessSocketConfigWrapper.WirelessSocketConfigWrapperBuilder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = WirelessSocketConfigWrapperBuilder.class)
public class WirelessSocketConfigWrapper {
    private final DeviceWatcherConfig deviceWatcher;
    private final WirelessSocketConfig wirelessSocket;

    @JsonPOJOBuilder(withPrefix = "")
    static final class WirelessSocketConfigWrapperBuilder {
    }
}
