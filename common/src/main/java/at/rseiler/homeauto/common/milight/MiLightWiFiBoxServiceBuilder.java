package at.rseiler.homeauto.common.milight;

import at.rseiler.homeauto.common.milight.config.MiLightWiFiBoxConfig;
import at.rseiler.homeauto.common.network.IpFromTo;
import com.google.common.io.CharStreams;
import de.toman.milight.WiFiBox;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Builder for the MiLightWiFiBoxService.
 */
@RequiredArgsConstructor
public class MiLightWiFiBoxServiceBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(MiLightWiFiBoxServiceBuilder.class);
    private static final String WIFI_BOX_CONTENT = "<HTML><HEAD><TITLE>401 Unauthorized</TITLE></HEAD>\n" +
            "<BODY BGCOLOR=\"#cc9999\"><H4>401 Unauthorized</H4>\n" +
            "Authorization required.\n" +
            "</BODY></HTML>\n";
    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
            .setConnectTimeout(1000)
            .setConnectionRequestTimeout(1000)
            .build();
    private static final HttpClient HTTP_CLIENT = HttpClientBuilder.create()
            .setDefaultRequestConfig(REQUEST_CONFIG)
            .build();
    private final MiLightWiFiBoxConfig config;

    /**
     * Builds the MiLightWiFiBoxService.
     *
     * @return an optional MiLightWiFiBoxService instance
     */
    public Optional<MiLightWiFiBoxService> build() {
        IpFromTo ipFromTo = new IpFromTo(config.getIpFrom(), config.getIpTo());

        for (String ip : ipFromTo) {
            if (isMiLightServer("http://" + ip)) {
                return createWiFiBox(ip).map(MiLightWiFiBoxService::new);
            }
        }

        return Optional.empty();
    }

    private boolean isMiLightServer(String uri) {
        try {
            HttpGet method = new HttpGet(uri);
            HttpResponse httpResponse = HTTP_CLIENT.execute(method);
            String message = CharStreams.toString(new InputStreamReader(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8));
            EntityUtils.consume(httpResponse.getEntity());

            return "HTTPD".equals(httpResponse.getFirstHeader("Server").getValue()) &&
                    "Basic realm=\"USER LOGIN\"".equals(httpResponse.getFirstHeader("WWW-Authenticate").getValue()) &&
                    WIFI_BOX_CONTENT.equals(message);
        } catch (Exception e) {
            LOGGER.trace("Failed to reach host: " + uri, e);
        }

        return false;
    }

    private Optional<WiFiBox> createWiFiBox(String ip) {
        try {
            return Optional.of(new WiFiBox(ip, 8899));
        } catch (UnknownHostException e) {
            LOGGER.trace("Failed to create WiFiBox: " + ip, e);
        }

        return Optional.empty();
    }
}
