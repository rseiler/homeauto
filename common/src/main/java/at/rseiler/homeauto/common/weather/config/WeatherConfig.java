package at.rseiler.homeauto.common.weather.config;

import at.rseiler.homeauto.common.weather.config.WeatherConfig.WeatherConfigBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = WeatherConfigBuilder.class)
public class WeatherConfig {
    @JsonProperty(required = true)
    private final String uri;

    @JsonProperty(required = true)
    private final String sunriseRegEx;

    @JsonProperty(required = true)
    private final String sunsetRegEx;

    @JsonPOJOBuilder(withPrefix = "")
    static final class WeatherConfigBuilder {
    }
}
