package at.rseiler.homeauto.common.watcher.config;

import at.rseiler.homeauto.common.watcher.config.DeviceWatcherConfig.DeviceWatcherConfigBuilder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonDeserialize(builder = DeviceWatcherConfigBuilder.class)
public class DeviceWatcherConfig {
    private final String ipFrom;
    private final String ipTo;
    private final long ttl;
    private final long sleepTime;
    private final List<String> macAddress;

    @JsonPOJOBuilder(withPrefix = "")
    static final class DeviceWatcherConfigBuilder {
    }
}
