package at.rseiler.homeauto.milight;

import at.rseiler.homeauto.common.HttpUtil;
import at.rseiler.homeauto.common.milight.MiLightCommand;
import at.rseiler.homeauto.common.milight.MiLightWiFiBoxService;
import at.rseiler.homeauto.common.milight.MiLightWiFiBoxServiceBuilder;
import at.rseiler.homeauto.common.watcher.DeviceWatcher;
import at.rseiler.homeauto.common.watcher.DeviceWatcher.DeviceEvent;
import at.rseiler.homeauto.common.watcher.DeviceWatcher.State;
import at.rseiler.homeauto.common.weather.WeatherService;
import at.rseiler.homeauto.milight.config.MiLightConfig;
import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Scans if a specific device is turned on and turns on the MiLight if it's after the sunset or schedules to turn
 * on the MiLight after the sunset.
 */
@RequiredArgsConstructor
public class MiLightApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(MiLightApp.class);
    private final MiLightConfig config;
    private final DeviceWatcher deviceWatcher;
    private final MiLightWiFiBoxServiceBuilder miLightWiFiBoxServiceBuilder;
    private MiLightWiFiBoxService miLightWiFiBoxService;
    private WeatherService weatherService;
    private Timer timer = new Timer();

    /**
     * Starts listening to the {@link MiLightApp#deviceWatcher}'s events and connects to the MiLight-WiFi-Box.
     */
    public void start() {
        LOGGER.info("MiLight: started");
        weatherService = new WeatherService(config.getWeather());
        Optional<MiLightWiFiBoxService> miLightWiFiBoxServiceOptional = miLightWiFiBoxServiceBuilder.build();

        if (miLightWiFiBoxServiceOptional.isPresent()) {
            miLightWiFiBoxService = miLightWiFiBoxServiceOptional.get();
        } else {
            LOGGER.error("MiLight: failed to connect to wifi box");
        }

        deviceWatcher.addObserver((observable, arg) -> eventHandler((DeviceEvent) arg));
    }

    private void eventHandler(DeviceEvent deviceEvent) {
        if (config.getMacAddress().contains(deviceEvent.getMac())) {
            if (deviceEvent.getState() == State.ON) {
                LocalTime sunsetTime = weatherService.getSunsetTime();

                if (localTime().isAfter(sunsetTime) || localTime().isBefore(LocalTime.of(6, 0))) {
                    turnOnLight();
                } else {
                    scheduleTurnOnLight(sunsetTime);
                }
            } else {
                timer.cancel();
            }
        }

    }

    private void turnOnLight() {
        LOGGER.info("Turned on the light");
        config.getRest().getUri().forEach(this::execRestCommand);
        config.getWifiBox().getCommands().forEach(this::execWifiBoxCommand);
    }

    private void scheduleTurnOnLight(LocalTime sunsetTime) {
        LOGGER.info("Schedule to turn on the light");
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TurnOnLightTimerTask(), ChronoUnit.MILLIS.between(localTime(), sunsetTime));
    }

    private void execRestCommand(String uri) {
        HttpUtil.post(uri, config.getRest().getAuthorization());
        commandWait();
    }

    private void execWifiBoxCommand(String command) {
        miLightWiFiBoxService.exec(MiLightCommand.fromString(command));
        commandWait();
    }

    private void commandWait() {
        try {
            Thread.sleep(config.getCommandWait());
        } catch (InterruptedException ignore) {
            // ignore
        }
    }

    @VisibleForTesting
    LocalTime localTime() {
        return LocalTime.now();
    }

    private class TurnOnLightTimerTask extends TimerTask {
        @Override
        public void run() {
            turnOnLight();
        }
    }
}
