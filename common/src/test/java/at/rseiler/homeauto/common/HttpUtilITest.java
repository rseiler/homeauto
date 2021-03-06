package at.rseiler.homeauto.common;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class HttpUtilITest {
    @ClassRule
    public static WireMockClassRule WIRE_MOCK_RULE = new WireMockClassRule(WireMockConfiguration.options().dynamicPort().usingFilesUnderClasspath("wiremock"));

    @Rule
    public WireMockClassRule wireMockRule = WIRE_MOCK_RULE;
    private int port;

    @Before
    public void setUp() throws Exception {
        port = wireMockRule.port();
    }

    @Test
    public void testGet() {
        String response = HttpUtil.get("http://localhost:" + port + "/wien/");

        verify(getRequestedFor(urlEqualTo("/wien/")));
        assertThat(response.length(), is(28708));
    }

    @Test
    public void testPost() {
        HttpUtil.post("http://localhost:" + port + "/milight/white/4", "Basic cmVpbmhhcmQ6bWVpblNpY2hlcmVzUFdEITIy");

        verify(postRequestedFor(urlEqualTo("/milight/white/4")));
    }
}