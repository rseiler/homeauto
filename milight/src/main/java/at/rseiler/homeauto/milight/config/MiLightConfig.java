package at.rseiler.homeauto.milight.config;

import at.rseiler.homeauto.common.milight.config.MiLightWiFiBoxConfig;
import at.rseiler.homeauto.common.weather.config.WeatherConfig;
import at.rseiler.homeauto.milight.config.MiLightConfig.MiLightConfigBuilder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonDeserialize(builder = MiLightConfigBuilder.class)
public class MiLightConfig {
    private final long commandWait;
    private final WeatherConfig weather;
    private final MiLightWiFiBoxConfig wifiBox;
    private final RestConfig rest;
    private final List<String> macAddress;

    @JsonPOJOBuilder(withPrefix = "")
    static final class MiLightConfigBuilder {
    }
}
