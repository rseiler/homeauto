package at.rseiler.homeauto.common.weather.config;

import at.rseiler.homeauto.common.weather.config.WeatherConfig.WeatherConfigBuilder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = WeatherConfigBuilder.class)
public class WeatherConfig {
    private final String uri;
    private final String sunriseRegEx;
    private final String sunsetRegEx;

    @JsonPOJOBuilder(withPrefix = "")
    static final class WeatherConfigBuilder {
    }
}
