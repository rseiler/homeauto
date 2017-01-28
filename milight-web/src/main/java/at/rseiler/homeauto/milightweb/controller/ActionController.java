package at.rseiler.homeauto.milightweb.controller;

import at.rseiler.homeauto.common.milight.MiLightCommand;
import at.rseiler.homeauto.common.milight.MiLightWiFiBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

/**
 * Listens to the HTTP requests and passes them to {@link MiLightWiFiBoxService#exec(MiLightCommand)}.
 */
@RestController
public class ActionController {

    private final MiLightWiFiBoxService miLightWiFiBoxService;

    @Autowired
    public ActionController(MiLightWiFiBoxService miLightWiFiBoxService) {
        this.miLightWiFiBoxService = miLightWiFiBoxService;
    }

    @PutMapping("/exec")
    public ResponseEntity<Void> exec(@RequestBody String[] command) {
        boolean success = miLightWiFiBoxService.exec(MiLightCommand.fromString(String.join(" ", command)));
        return new ResponseEntity<>(success ? OK : INTERNAL_SERVER_ERROR);
    }
}
