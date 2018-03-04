package at.rseiler.homeauto.milight;

import at.rseiler.homeauto.arduino.Arduino;
import at.rseiler.homeauto.arduino.Arduino.ArduinoAction;
import at.rseiler.homeauto.common.HttpUtil;
import at.rseiler.homeauto.common.milight.MiLightCommand;
import at.rseiler.homeauto.common.milight.MiLightWiFiBoxService;
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
    private final Arduino arduino;
    private final DeviceWatcher deviceWatcher;
    private final MiLightWiFiBoxService miLightWiFiBoxService;
    private WeatherService weatherService;
    private Timer turnOnLightTimer = new Timer();
    private Timer turnOffLightTimer = new Timer();

    /**
     * Starts listening to the {@link MiLightApp#deviceWatcher}'s events and connects to the MiLight-WiFi-Box.
     */
    public void start() {
        LOGGER.info("MiLight: started");
        weatherService = new WeatherService(config.getWeather());
        deviceWatcher.subscribe(this::eventHandler);

        if (arduino != null) {
            arduino.subscribe(this::arduinoHandler);
        }
    }

    private void arduinoHandler(ArduinoAction arduinoAction) {
        LocalTime sunsetTime = weatherService.getSunsetTime();

        switch (arduinoAction) {
            case MOVEMENT:
                if (localTime().isAfter(sunsetTime) || localTime().isBefore(LocalTime.of(6, 0))) {
                    execWifiBoxCommand("on 4");
                    scheduleTurnOffLight();
                }
                break;
            case ENTER:
                if (localTime().isAfter(sunsetTime) || localTime().isBefore(LocalTime.of(6, 0))) {
                    turnOnLight();
                } else {
                    scheduleTurnOnLight(sunsetTime);
                }
                break;
            case LEAVE:
                LOGGER.info("Turn off the light");
                turnOnLightTimer.cancel();
                execWifiBoxCommand("off");
                break;
            default:
                LOGGER.warn("Unexpected Arduino command: " + arduinoAction);
                break;
        }
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
                turnOnLightTimer.cancel();
            }
        }
    }

    private void turnOnLight() {
        LOGGER.info("Turn on the light");

        if (config.getRest() != null) {
            config.getRest().getUri().forEach(this::execRestCommand);
        }

        if (config.getWifiBox() != null) {
            config.getWifiBox().getCommands().forEach(this::execWifiBoxCommand);
        }

        scheduleTurnOffLight();
    }

    private void scheduleTurnOffLight() {
        turnOffLightTimer.cancel();
        turnOffLightTimer = new Timer();
        turnOffLightTimer.schedule(new TurnOffLightTimerTask(), 300_000L);
    }

    private void turnOffLight() {
        execWifiBoxCommand("off 4");
    }

    private void scheduleTurnOnLight(LocalTime sunsetTime) {
        LOGGER.info("Schedule to turn on the light");
        turnOnLightTimer.cancel();
        turnOnLightTimer = new Timer();
        turnOnLightTimer.schedule(new TurnOnLightTimerTask(), ChronoUnit.MILLIS.between(localTime(), sunsetTime));
    }

    private void execRestCommand(String uri) {
        HttpUtil.post(uri, config.getRest().getAuthorization());
        commandWait();
    }

    private void execWifiBoxCommand(String command) {
        LOGGER.info("wifi box cmd: " + command);
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

    private class TurnOffLightTimerTask extends TimerTask {
        @Override
        public void run() {
            turnOffLight();
        }
    }
}
