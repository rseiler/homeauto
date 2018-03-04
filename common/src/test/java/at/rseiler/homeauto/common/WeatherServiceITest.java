package at.rseiler.homeauto.common;

import at.rseiler.homeauto.common.fortest.FileUtil;
import at.rseiler.homeauto.common.weather.WeatherService;
import at.rseiler.homeauto.common.weather.config.WeatherConfig;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import lombok.Builder;
import lombok.Value;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class WeatherServiceITest {
    @ClassRule
    public static WireMockClassRule WIRE_MOCK_RULE = new WireMockClassRule(WireMockConfiguration.options().port(51125).usingFilesUnderClasspath("wiremock"));

    @Rule
    public WireMockClassRule wireMockRule = WIRE_MOCK_RULE;
    
    @Test
    public void test() throws IOException {
        File yml = FileUtil.get("common", "src/test/resources/weather.yml");
        WeatherConfigWrapper weatherConfig = YmlUtil.read(yml, WeatherConfigWrapper.class);
        WeatherService weatherService = new WeatherService(weatherConfig.getWeather());

        assertTrue(weatherService.getSunriseTime().isBefore(weatherService.getSunsetTime()));
    }

    @Value
    @Builder
    @JsonDeserialize(builder = WeatherConfigWrapper.WeatherConfigWrapperBuilder.class)
    static class WeatherConfigWrapper {
        private final WeatherConfig weather;

        @JsonPOJOBuilder(withPrefix = "")
        static final class WeatherConfigWrapperBuilder {
        }
    }
}
