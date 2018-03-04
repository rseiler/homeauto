package at.rseiler.homeauto.milight.config;

import at.rseiler.homeauto.milight.config.RestConfig.RestConfigBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonDeserialize(builder = RestConfigBuilder.class)
public class RestConfig {
    @JsonProperty(required = true)
    private final List<String> uri;
    private final String authorization;

    @JsonPOJOBuilder(withPrefix = "")
    static final class RestConfigBuilder {
    }
}
