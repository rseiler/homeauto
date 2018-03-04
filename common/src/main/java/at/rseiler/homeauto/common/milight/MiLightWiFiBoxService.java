package at.rseiler.homeauto.common.milight;

import de.toman.milight.WiFiBox;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Wrapper for the WiFiBox from de.toman.
 */
@SuppressWarnings("PMD")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MiLightWiFiBoxService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MiLightWiFiBoxService.class);
    private final WiFiBox wiFiBox;

    /**
     * Turns off all lights.
     *
     * @return true if the command was sent successfully
     */
    public boolean off() {
        try {
            wiFiBox.off();
            return true;
        } catch (IOException e) {
            LOGGER.error("WiFiBox exception", e);
        }

        return false;
    }

    /**
     * Turns off the specified light group.
     *
     * @param group the light group: 1-4
     * @return true if the command was sent successfully
     */
    public boolean off(int group) {
        try {
            wiFiBox.off(group);
            return true;
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.error("WiFiBox exception", e);
        }

        return false;
    }

    /**
     * Turns on all lights.
     *
     * @return true if the command was sent successfully
     */
    public boolean on() {
        try {
            wiFiBox.on();
            return true;
        } catch (IOException e) {
            LOGGER.error("WiFiBox exception", e);
        }

        return false;
    }

    /**
     * Turns on the specified light group.
     *
     * @param group the light group: 1-4
     * @return true if the command was sent successfully
     */
    public boolean on(int group) {
        try {
            wiFiBox.on(group);
            return true;
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.error("WiFiBox exception", e);
        }

        return false;
    }

    /**
     * Turns on the white mode for all lights.
     *
     * @return true if the command was sent successfully
     */
    public boolean white() {
        wiFiBox.white();
        return true;
    }

    /**
     * Turns on the white mode for the specified light group.
     *
     * @param group the light group: 1-4
     * @return true if the command was sent successfully
     */
    public boolean white(int group) {
        try {
            wiFiBox.white(group);
            return true;
        } catch (IllegalArgumentException e) {
            LOGGER.error("WiFiBox exception", e);
        }

        return false;
    }

    /**
     * Turns on the disco mode for all lights.
     *
     * @return true if the command was sent successfully
     */
    public boolean discoMode() {
        try {
            wiFiBox.discoMode();
            return true;
        } catch (IOException e) {
            LOGGER.error("WiFiBox exception", e);
        }

        return false;
    }

    /**
     * Turns on the disco mode for the specified light group.
     *
     * @param group the light group: 1-4
     * @return true if the command was sent successfully
     */
    public boolean discoMode(int group) {
        try {
            wiFiBox.discoMode(group);
            return true;
        } catch (IllegalArgumentException e) {
            LOGGER.error("WiFiBox exception", e);
        }

        return false;
    }

    /**
     * Increases the disco mode speed.
     *
     * @return true if the command was sent successfully
     */
    public boolean discoModeFaster() {
        try {
            wiFiBox.discoModeFaster();
            return true;
        } catch (IOException e) {
            LOGGER.error("WiFiBox exception", e);
        }

        return false;
    }

    /**
     * Decreases the disco mode speed.
     *
     * @return true if the command was sent successfully
     */
    public boolean discoModeSlower() {
        try {
            wiFiBox.discoModeSlower();
            return true;
        } catch (IOException e) {
            LOGGER.error("WiFiBox exception", e);
        }

        return false;
    }

    /**
     * Sets the brightness to the specified value for all lights.
     *
     * @param value the brightness value: 1-59
     * @return true if the command was sent successfully
     */
    public boolean brightness(int value) {
        try {
            wiFiBox.brightness(value);
            return true;
        } catch (IOException e) {
            LOGGER.error("WiFiBox exception", e);
        }

        return false;
    }

    /**
     * Sets the brightness to the specified value for the specified light group.
     *
     * @param group the light group: 1-4
     * @param value the brightness value: 1-59
     * @return true if the command was sent successfully
     */
    public boolean brightness(int group, int value) {
        try {
            wiFiBox.brightness(group, value);
            return true;
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.error("WiFiBox exception", e);
        }

        return false;
    }

    /**
     * Sets the color to the specified value for all lights.
     *
     * @param value the brightness value: 1-255
     * @return true if the command was sent successfully
     */
    public boolean color(int value) {
        try {
            wiFiBox.color(value);
            return true;
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.error("WiFiBox exception", e);
        }

        return false;
    }

    /**
     * Sets the color to the specified value for the specified light group.
     *
     * @param group the light group: 1-4
     * @param value the brightness value: 1-255
     * @return true if the command was sent successfully
     */
    public boolean color(int group, int value) {
        try {
            wiFiBox.color(group, value);
            return true;
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.error("WiFiBox exception", e);
        }

        return false;
    }

    /**
     * Interprets the command parameter and calls the appropriate method with the arguments.
     *
     * @param command specifies the method which should be called and the parameters
     * @return true if the command was sent successfully
     */
    public boolean exec(MiLightCommand command) {
        try {
            Class<?>[] paramsArray = createParamsArray(command);
            Method method = MiLightWiFiBoxService.class.getMethod(command.getCommand(), paramsArray);
            return (boolean) method.invoke(this, (Object[]) createParams(command));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.debug("Method doesn't exist: " + command, e);
        }

        return false;
    }

    private Class<?>[] createParamsArray(MiLightCommand miLightCommand) {
        Class<?>[] paramTypes = new Class<?>[miLightCommand.getArgumentCount()];
        Arrays.fill(paramTypes, int.class);
        return paramTypes;
    }

    private Integer[] createParams(MiLightCommand miLightCommand) {
        Integer[] paramTypes = new Integer[miLightCommand.getArgumentCount()];

        if (miLightCommand.getGroup() != null) {
            paramTypes[0] = miLightCommand.getGroup();
        }
        if (miLightCommand.getValue() != null) {
            paramTypes[1] = miLightCommand.getValue();
        }

        return paramTypes;
    }
}
