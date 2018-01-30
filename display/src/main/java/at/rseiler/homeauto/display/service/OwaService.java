package at.rseiler.homeauto.display.service;

import at.rseiler.homeauto.display.model.CalendarEntry;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static at.rseiler.homeauto.display.DisplayApp.CACHE_OWA;

@Service
public class OwaService {

    private static final Pattern RGITM_PATTERN = Pattern.compile("\"rgItm\":\\[(.*?)\\]");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss");
    private static final Logger LOGGER = LoggerFactory.getLogger(OwaService.class);
    private final HttpClientContext httpClientContext = HttpClientContext.create();
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final String url;
    private final String userName;
    private final String password;
    private LocalDateTime lastLogin = LocalDateTime.MIN;

    @Autowired
    public OwaService(@Value("${owa.url}") String url, @Value("${owa.username}") String userName, @Value("${owa.password}") String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    @Cacheable(CACHE_OWA)
    public List<CalendarEntry> getCalendarData() throws IOException {
        if (ChronoUnit.MINUTES.between(lastLogin, LocalDateTime.now()) >= 30) {
            login();
        }

        String userContext = httpClientContext.getCookieStore().getCookies().stream()
                .filter(cookie -> "UserContext".equals(cookie.getName()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Failed to get UserContext"))
                .getValue();

        HttpPost httpPost = new HttpPost("https://webgraz.styria-it.com/owa/ev.owa?oeh=1&ns=CalendarView&ev=GetViewPayload");
        httpPost.setEntity(new StringEntity("<params><canary>" + userContext + "</canary><fId><item>LgAAAABqtUsvDAoLQLlOZivkU+a4AQC4MY36W+ldTbvORMqNIzyDAAAA+ECkAAAC</item></fId><days><item>2018-01-29T00:00:00</item></days><vt>3</vt></params>"));
        CloseableHttpResponse response = sendRequest(httpPost);
        String javaScript = EntityUtils.toString(response.getEntity());
        Matcher matcher = RGITM_PATTERN.matcher(javaScript);

        if (matcher.find()) {
            String itemsJavaScript = matcher.group(1);
            String[] items = itemsJavaScript.substring(9).split("\\),new Item\\(");
            return Arrays.stream(items)
                    .map(item -> item.replaceAll("\"", ""))
                    .map(item -> item.split(","))
                    .map(itemValues -> new CalendarEntry(
                            itemValues[7],
                            LocalDateTime.parse(itemValues[3] + itemValues[4].replace(" UTC", ""), DATE_TIME_FORMATTER),
                            LocalDateTime.parse(itemValues[5] + itemValues[6].replace(" UTC", ""), DATE_TIME_FORMATTER),
                            itemValues[8],
                            itemValues[14]
                    ))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private void login() {
        lastLogin = LocalDateTime.now();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("curl", "L2fowa"));
        nvps.add(new BasicNameValuePair("curlid", "4037518629-978737028"));
        nvps.add(new BasicNameValuePair("curlmode", "0"));
        nvps.add(new BasicNameValuePair("pubpriv", "0"));
        nvps.add(new BasicNameValuePair("passphrase", ""));
        nvps.add(new BasicNameValuePair("pin", ""));
        nvps.add(new BasicNameValuePair("submit", "Log+On"));
        nvps.add(new BasicNameValuePair("username", userName));
        nvps.add(new BasicNameValuePair("password", password));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Error", e);
        }

        try (CloseableHttpResponse response = sendRequest(httpPost)) {
            if (response.getStatusLine().getStatusCode() == 302) {
                owa();
            } else {
                LOGGER.error("Login failed");
            }
        } catch (IOException e) {
            LOGGER.error("Error", e);
        }
    }

    private void owa() {
        HttpGet httpGet = new HttpGet("https://webgraz.styria-it.com/owa/");
        try {
            CloseableHttpResponse response = sendRequest(httpGet);
            response.close();
        } catch (IOException e) {
            LOGGER.error("Error", e);
        }
    }

    private CloseableHttpResponse sendRequest(HttpRequestBase request) {
        CloseableHttpResponse response = null;

        try {
            response = httpClient.execute(request, httpClientContext);

            if (response.containsHeader("X-OWA-Error")) {
                LOGGER.error("X-OWA-Error: ", response.getFirstHeader("X-OWA-Error").getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
