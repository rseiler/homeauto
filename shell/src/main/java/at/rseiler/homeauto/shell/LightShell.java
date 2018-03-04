package at.rseiler.homeauto.shell;

import at.rseiler.homeauto.common.milight.MiLightWiFiBoxService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class LightShell {
    private final MiLightWiFiBoxService miLightWiFiBoxService;

    public LightShell(MiLightWiFiBoxService miLightWiFiBoxService) {
        this.miLightWiFiBoxService = miLightWiFiBoxService;
    }

    @ShellMethod("turns the light on. either the specified light or all lights.")
    public void lightOn(@ShellOption(defaultValue = "-1") int group) {
        if (group == -1) {
            miLightWiFiBoxService.on();
        } else {
            miLightWiFiBoxService.on(group);
        }
    }

    @ShellMethod("turns the light off. either the specified light or all lights.")
    public void lightOff(@ShellOption(defaultValue = "-1") int group) {
        if (group == -1) {
            miLightWiFiBoxService.off();
        } else {
            miLightWiFiBoxService.off(group);
        }
    }

}
