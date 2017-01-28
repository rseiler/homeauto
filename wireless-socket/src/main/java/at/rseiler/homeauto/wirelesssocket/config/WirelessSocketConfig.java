package at.rseiler.homeauto.wirelesssocket.config;

import at.rseiler.homeauto.wirelesssocket.config.WirelessSocketConfig.WirelessSocketConfigBuilder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonDeserialize(builder = WirelessSocketConfigBuilder.class)
public class WirelessSocketConfig {
    private final int sendCount;
    private final long sendSleep;
    private final List<String> macAddress;

    @JsonPOJOBuilder(withPrefix = "")
    static final class WirelessSocketConfigBuilder {
    }

}
