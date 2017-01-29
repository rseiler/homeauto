package at.rseiler.homeauto.common.milight.config;

import at.rseiler.homeauto.common.milight.config.MiLightWiFiBoxConfig.MiLightWiFiBoxConfigBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@RequiredArgsConstructor
@JsonDeserialize(builder = MiLightWiFiBoxConfigBuilder.class)
public class MiLightWiFiBoxConfig {
    @JsonProperty(required = true)
    private final String ipFrom;

    @JsonProperty(required = true)
    private final String ipTo;

    @JsonProperty(required = true)
    private final List<String> commands;

    @JsonPOJOBuilder(withPrefix = "")
    static final class MiLightWiFiBoxConfigBuilder {
    }
}
