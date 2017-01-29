package at.rseiler.homeauto.wirelesssocket.config;

import at.rseiler.homeauto.wirelesssocket.config.WirelessSocketConfig.WirelessSocketConfigBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonDeserialize(builder = WirelessSocketConfigBuilder.class)
public class WirelessSocketConfig {
    @JsonProperty(required = true)
    private final int sendCount;

    @JsonProperty(required = true)
    private final long sendSleep;

    @JsonProperty(required = true)
    private final List<String> macAddress;

    @JsonPOJOBuilder(withPrefix = "")
    static final class WirelessSocketConfigBuilder {
    }

}
