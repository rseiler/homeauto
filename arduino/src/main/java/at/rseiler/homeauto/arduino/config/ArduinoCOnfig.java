package at.rseiler.homeauto.arduino.config;


import at.rseiler.homeauto.arduino.config.ArduinoConfig.ArduinoConfigBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonDeserialize(builder = ArduinoConfigBuilder.class)
public class ArduinoConfig {
    @JsonProperty(required = true)
    private final List<String> port;

    @JsonPOJOBuilder(withPrefix = "")
    static final class ArduinoConfigBuilder {
    }
}
