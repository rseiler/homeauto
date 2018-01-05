package at.rseiler.homeauto.arduino;

import at.rseiler.homeauto.arduino.config.ArduinoConfig;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import jssc.SerialPortException;
import lombok.RequiredArgsConstructor;
import org.ardulink.core.AbstractConnection;
import org.ardulink.core.Connection.Listener;
import org.ardulink.core.Connection.ListenerAdapter;
import org.ardulink.core.ConnectionBasedLink;
import org.ardulink.core.Link;
import org.ardulink.core.serial.jssc.SerialLinkConfig;
import org.ardulink.core.serial.jssc.SerialLinkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class Arduino {
    private static final Logger LOGGER = LoggerFactory.getLogger(Arduino.class);
    private final EventBus eventBus = new EventBus();
    private final ArduinoConfig arduinoConfig;

    public void start() {
        LOGGER.info("Arduino: starting...");
        boolean successful = false;

        for (String port : arduinoConfig.getPort()) {
            try {
                SerialLinkConfig config = new SerialLinkConfig();
                config.setBaudrate(9600);
                config.setPort(port);
                config.setPingprobe(false);
                config.setWaitsecs(1);
                Link link = new SerialLinkFactory().newLink(config).getDelegate();
                AbstractConnection connection = (AbstractConnection) ((ConnectionBasedLink) link).getConnection();
                connection.getListeners().forEach(connection::removeListener);
                connection.addListener(rawDataListener());
                successful = true;
                break;
            } catch (IOException | SerialPortException e) {
                LOGGER.info("Arduino: not found on port: {}", port);
            }
        }

        if (successful) {
            LOGGER.info("Arduino: started");
        } else {
            LOGGER.error("Arduino: failed to start");
        }
    }

    public void subscribe(Consumer<ArduinoAction> consumer) {
        eventBus.register(new ArduinoEventSubscriber(consumer));
    }

    private void fireEvent(ArduinoAction arduinoAction) {
        LOGGER.debug("ArduinoAction: {}", arduinoAction);
        eventBus.post(arduinoAction);
    }

    private Listener rawDataListener() {
        return new ListenerAdapter() {
            @Override
            public void received(byte[] bytes) {
                String command = new String(bytes);
                LOGGER.info(command);

                if (command.contains("entering")) {
                    fireEvent(ArduinoAction.ENTERING);
                } else if (command.contains("leaving")) {
                    fireEvent(ArduinoAction.LEAVING);
                }
            }
        };
    }

    public enum ArduinoAction {
        ENTERING, LEAVING
    }

    @RequiredArgsConstructor
    private static class ArduinoEventSubscriber {
        private final Consumer<ArduinoAction> consumer;

        @Subscribe
        public void deviceEvent(ArduinoAction deviceEvent) {
            consumer.accept(deviceEvent);
        }
    }
}
