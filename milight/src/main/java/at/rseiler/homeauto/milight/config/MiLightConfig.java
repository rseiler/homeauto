package at.rseiler.homeauto.milight.config;

import at.rseiler.homeauto.common.milight.config.MiLightWiFiBoxConfig;
import at.rseiler.homeauto.common.weather.config.WeatherConfig;
import at.rseiler.homeauto.milight.config.MiLightConfig.MiLightConfigBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonDeserialize(builder = MiLightConfigBuilder.class)
public class MiLightConfig {
    @JsonProperty(required = true)
    private final long commandWait;
    @JsonProperty(required = true)
    private final WeatherConfig weather;
    @JsonProperty(required = true)
    private final List<String> macAddress;
    private final RestConfig rest;
    private final MiLightWiFiBoxConfig wifiBox;

    @JsonPOJOBuilder(withPrefix = "")
    static final class MiLightConfigBuilder {
    }
}
