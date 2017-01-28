package at.rseiler.homeauto.milight;

import at.rseiler.homeauto.common.YmlUtil;
import at.rseiler.homeauto.common.fortest.FileUtil;
import at.rseiler.homeauto.common.milight.ForTestMiLightWiFiBoxService;
import at.rseiler.homeauto.common.milight.MiLightWiFiBoxService;
import at.rseiler.homeauto.common.milight.MiLightWiFiBoxServiceBuilder;
import at.rseiler.homeauto.common.watcher.DeviceWatcher.DeviceEvent;
import at.rseiler.homeauto.common.watcher.DeviceWatcher.State;
import at.rseiler.homeauto.common.watcher.ForTestDeviceWatcher;
import at.rseiler.homeauto.milight.config.MiLightConfigWrapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import de.toman.milight.WiFiBox;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.time.LocalTime;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class MiLightAppTest {
    private static final int TIMEOUT_VALUE = 200;
    private static final LocalTime TIME_BEFORE_SUNSET = LocalTime.of(16, 47, 59, 998_000_000);
    private static final RequestPatternBuilder REQUEST_PATTERN_BUILDER1 = postRequestedFor(urlEqualTo("/milight/white/3"));
    private static final RequestPatternBuilder REQUEST_PATTERN_BUILDER2 = postRequestedFor(urlEqualTo("/milight/white/4"));

    @ClassRule
    public static WireMockClassRule WIRE_MOCK_RULE = new WireMockClassRule(options().port(51125).usingFilesUnderClasspath("wiremock"));

    @Rule
    public WireMockClassRule wireMockRule = WIRE_MOCK_RULE;

    private MiLightApp miLightApp;
    private ForTestDeviceWatcher deviceWatcher;
    private WiFiBox wiFiBox;

    @Before
    public void setUp() throws Exception {
        File yml = FileUtil.get("milight", "src/test/resources/milight.yml");
        MiLightConfigWrapper miLightConfigWrapper = YmlUtil.read(yml, MiLightConfigWrapper.class);
        deviceWatcher = new ForTestDeviceWatcher(null);
        MiLightWiFiBoxServiceBuilder miLightWiFiBoxServiceBuilder = mock(MiLightWiFiBoxServiceBuilder.class);
        wiFiBox = mock(WiFiBox.class);
        MiLightWiFiBoxService miLightWiFiBoxService = new ForTestMiLightWiFiBoxService(wiFiBox);
        when(miLightWiFiBoxServiceBuilder.build()).thenReturn(Optional.of(miLightWiFiBoxService));
        miLightApp = spy(new MiLightApp(miLightConfigWrapper.getMiLight(), deviceWatcher, miLightWiFiBoxServiceBuilder));
        miLightApp.start();
    }

    @Test
    public void testTurnOnLightsAfterSunset() throws Exception {
        doReturn(LocalTime.of(18, 0)).when(miLightApp).localTime();

        deviceWatcher.fireEvent(new DeviceEvent("127.0.0.1", "a1-23-bc-4d-56-e7", State.ON));

        verify(wiFiBox).white(1);
        verify(wiFiBox).color(3, 111);
        WireMock.verify(REQUEST_PATTERN_BUILDER1);
        WireMock.verify(REQUEST_PATTERN_BUILDER2);
    }

    @Test
    public void testTurnOnLightsBeforeSix() throws Exception {
        doReturn(LocalTime.of(5, 59)).when(miLightApp).localTime();

        deviceWatcher.fireEvent(new DeviceEvent("127.0.0.1", "a1-23-bc-4d-56-e7", State.ON));

        verify(wiFiBox).white(1);
        verify(wiFiBox).color(3, 111);
        WireMock.verify(REQUEST_PATTERN_BUILDER1);
        WireMock.verify(REQUEST_PATTERN_BUILDER2);
    }

    @Test
    public void testScheduleTurnOnLight() throws Exception {
        doReturn(TIME_BEFORE_SUNSET).when(miLightApp).localTime();

        deviceWatcher.fireEvent(new DeviceEvent("127.0.0.1", "a1-23-bc-4d-56-e7", State.ON));

        verify(wiFiBox, times(0)).white(1);
        verify(wiFiBox, times(0)).color(3, 111);
        assertThat(wireMockRule.countRequestsMatching(REQUEST_PATTERN_BUILDER1.build()).getCount(), is(0));
        assertThat(wireMockRule.countRequestsMatching(REQUEST_PATTERN_BUILDER2.build()).getCount(), is(0));

        verify(wiFiBox, timeout(TIMEOUT_VALUE)).white(1);
        verify(wiFiBox, timeout(TIMEOUT_VALUE)).color(3, 111);
        WireMock.verify(REQUEST_PATTERN_BUILDER1);
        WireMock.verify(REQUEST_PATTERN_BUILDER2);
    }

    @Test
    public void testScheduleCancel() throws Exception {
        doReturn(TIME_BEFORE_SUNSET).when(miLightApp).localTime();

        deviceWatcher.fireEvent(new DeviceEvent("127.0.0.1", "a1-23-bc-4d-56-e7", State.ON));
        deviceWatcher.fireEvent(new DeviceEvent("127.0.0.1", "a1-23-bc-4d-56-e7", State.OFF));

        verify(wiFiBox, times(0)).white(1);
        verify(wiFiBox, times(0)).color(3, 111);
        WireMock.verify(0, REQUEST_PATTERN_BUILDER1);
        WireMock.verify(0, REQUEST_PATTERN_BUILDER2);
    }

    @Test
    public void testReschedule() throws Exception {
        doReturn(TIME_BEFORE_SUNSET).when(miLightApp).localTime();

        deviceWatcher.fireEvent(new DeviceEvent("127.0.0.1", "a1-23-bc-4d-56-e7", State.ON));
        deviceWatcher.fireEvent(new DeviceEvent("127.0.0.1", "a1-23-bc-4d-56-e7", State.OFF));
        deviceWatcher.fireEvent(new DeviceEvent("127.0.0.1", "a1-23-bc-4d-56-e7", State.ON));

        verify(wiFiBox, timeout(TIMEOUT_VALUE)).white(1);
        verify(wiFiBox, timeout(TIMEOUT_VALUE)).color(3, 111);
        WireMock.verify(REQUEST_PATTERN_BUILDER1);
        WireMock.verify(REQUEST_PATTERN_BUILDER2);
    }
}