package at.rseiler.homeauto.common.weather;

import at.rseiler.homeauto.common.HttpUtil;
import at.rseiler.homeauto.common.weather.config.WeatherConfig;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Calls a website and extracts the sunrise time and sunset time.
 */
public final class WeatherService {
    private final String uri;
    private final Pattern sunrisePattern;
    private final Pattern sunsetPattern;
    private WeatherData weatherData;

    public WeatherService(WeatherConfig weatherConfig) {
        this.uri = weatherConfig.getUri();
        this.sunrisePattern = Pattern.compile(weatherConfig.getSunriseRegEx());
        this.sunsetPattern = Pattern.compile(weatherConfig.getSunsetRegEx());
    }

    /**
     * Returns the sunrise time.
     *
     * @return the sunrise time
     */
    public LocalTime getSunriseTime() {
        updateData();
        return weatherData.sunrise;
    }

    /**
     * Returns the sunset time.
     *
     * @return the sunset time
     */
    public LocalTime getSunsetTime() {
        updateData();
        return weatherData.sunset;
    }

    private void updateData() {
        if (weatherData == null || weatherData.created < LocalDate.now().toEpochDay()) {
            String html = HttpUtil.get(uri);
            Matcher sunriseMatcher = sunrisePattern.matcher(html);
            Matcher sunsetMatcher = sunsetPattern.matcher(html);

            if (sunsetMatcher.find() && sunriseMatcher.find()) {
                LocalTime sunriseTime = LocalTime.of(Integer.parseInt(sunriseMatcher.group(1)), Integer.parseInt(sunriseMatcher.group(2)));
                LocalTime sunsetTime = LocalTime.of(Integer.parseInt(sunsetMatcher.group(1)), Integer.parseInt(sunsetMatcher.group(2)));

                weatherData = new WeatherData(sunriseTime, sunsetTime, System.currentTimeMillis());
            }
        }
    }

    @ToString
    @RequiredArgsConstructor
    private static final class WeatherData {
        private final LocalTime sunrise;
        private final LocalTime sunset;
        private final long created;
    }
}
