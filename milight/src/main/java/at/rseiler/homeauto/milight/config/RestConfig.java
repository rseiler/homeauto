package at.rseiler.homeauto.milight.config;

import at.rseiler.homeauto.milight.config.RestConfig.RestConfigBuilder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonDeserialize(builder = RestConfigBuilder.class)
public class RestConfig {
    private final String authorization;
    private final List<String> uri;

    @JsonPOJOBuilder(withPrefix = "")
    static final class RestConfigBuilder {
    }
}
