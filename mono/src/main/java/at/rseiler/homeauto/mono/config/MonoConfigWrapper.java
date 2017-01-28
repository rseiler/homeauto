package at.rseiler.homeauto.mono.config;


import at.rseiler.homeauto.common.watcher.config.DeviceWatcherConfig;
import at.rseiler.homeauto.milight.config.MiLightConfig;
import at.rseiler.homeauto.mono.config.MonoConfigWrapper.MonoConfigWrapperBuilder;
import at.rseiler.homeauto.wirelesssocket.config.WirelessSocketConfig;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = MonoConfigWrapperBuilder.class)
public class MonoConfigWrapper {
    private final DeviceWatcherConfig deviceWatcher;
    private final WirelessSocketConfig wirelessSocket;
    private final MiLightConfig miLight;

    @JsonPOJOBuilder(withPrefix = "")
    static final class MonoConfigWrapperBuilder {
    }
}
