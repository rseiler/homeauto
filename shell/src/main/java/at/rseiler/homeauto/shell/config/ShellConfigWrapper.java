package at.rseiler.homeauto.shell.config;


import at.rseiler.homeauto.common.watcher.config.DeviceWatcherConfig;
import at.rseiler.homeauto.milight.config.MiLightConfig;
import at.rseiler.homeauto.shell.config.ShellConfigWrapper.ShellConfigWrapperBuilder;
import at.rseiler.homeauto.wirelesssocket.config.WirelessSocketConfig;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = ShellConfigWrapperBuilder.class)
public class ShellConfigWrapper {
    @JsonProperty(required = true)
    private final DeviceWatcherConfig deviceWatcher;

    @JsonProperty(required = true)
    private final WirelessSocketConfig wirelessSocket;

    @JsonProperty(required = true)
    private final MiLightConfig miLight;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class ShellConfigWrapperBuilder {
    }
}
