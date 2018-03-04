package at.rseiler.homeauto.wirelesssocket.config;

import at.rseiler.homeauto.common.watcher.config.DeviceWatcherConfig;
import at.rseiler.homeauto.wirelesssocket.config.WirelessSocketConfigWrapper.WirelessSocketConfigWrapperBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = WirelessSocketConfigWrapperBuilder.class)
public class WirelessSocketConfigWrapper {
    @JsonProperty(required = true)
    private final DeviceWatcherConfig deviceWatcher;

    @JsonProperty(required = true)
    private final WirelessSocketConfig wirelessSocket;

    @JsonPOJOBuilder(withPrefix = "")
    static final class WirelessSocketConfigWrapperBuilder {
    }
}
