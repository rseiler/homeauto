package at.rseiler.homeauto.common.watcher.config;

import at.rseiler.homeauto.common.watcher.config.DeviceWatcherConfig.DeviceWatcherConfigBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonDeserialize(builder = DeviceWatcherConfigBuilder.class)
public class DeviceWatcherConfig {
    @JsonProperty(required = true)
    private final String ipFrom;

    @JsonProperty(required = true)
    private final String ipTo;

    @JsonProperty(required = true)
    private final long ttl;

    @JsonProperty(required = true)
    private final int retryCount;

    @JsonProperty(required = true)
    private final int threadCount;

    @JsonProperty(required = true)
    private final long sleepTime;

    @JsonProperty(required = true)
    private final List<String> macAddress;

    @JsonPOJOBuilder(withPrefix = "")
    static final class DeviceWatcherConfigBuilder {
    }
}
