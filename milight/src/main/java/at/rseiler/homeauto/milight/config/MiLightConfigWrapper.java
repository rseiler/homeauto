package at.rseiler.homeauto.milight.config;

import at.rseiler.homeauto.common.watcher.config.DeviceWatcherConfig;
import at.rseiler.homeauto.milight.config.MiLightConfigWrapper.MiLightConfigWrapperBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = MiLightConfigWrapperBuilder.class)
public class MiLightConfigWrapper {
    @JsonProperty(required = true)
    private final DeviceWatcherConfig deviceWatcher;
    @JsonProperty(required = true)
    private final MiLightConfig miLight;

    @JsonPOJOBuilder(withPrefix = "")
    static final class MiLightConfigWrapperBuilder {
    }
}
