package at.rseiler.homeauto.common.milight;

import at.rseiler.homeauto.common.milight.config.MiLightWiFiBoxConfig;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MiLightWiFiBoxServiceBuilderTest {
    @ClassRule
    public static WireMockClassRule WIRE_MOCK_RULE = new WireMockClassRule(WireMockConfiguration.options().port(80).usingFilesUnderClasspath("wiremock"));

    @Rule
    public WireMockClassRule wireMockRule = WIRE_MOCK_RULE;

    @Test
    public void buildSuccessful() throws Exception {
        MiLightWiFiBoxConfig config = new MiLightWiFiBoxConfig("127.0.0.1", "127.0.0.1", Collections.emptyList());
        Optional<MiLightWiFiBoxService> miLightWiFiBoxService = new MiLightWiFiBoxServiceBuilder(config).build();

        assertTrue(miLightWiFiBoxService.isPresent());
    }
    
    @Test
    public void buildFail() throws Exception {
        wireMockRule.stop();
        MiLightWiFiBoxConfig config = new MiLightWiFiBoxConfig("127.0.0.1", "127.0.0.1", Collections.emptyList());
        Optional<MiLightWiFiBoxService> miLightWiFiBoxService = new MiLightWiFiBoxServiceBuilder(config).build();

        assertFalse(miLightWiFiBoxService.isPresent());
    }
}
