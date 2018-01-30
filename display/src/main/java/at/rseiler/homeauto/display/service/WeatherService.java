package at.rseiler.homeauto.display.service;

import at.rseiler.homeauto.display.model.WeatherDataPoint;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static at.rseiler.homeauto.display.DisplayApp.CACHE_WEATHER;

@Service
public class WeatherService {
    private static final int WEATHER_POINTS = 16;
    private static final Pattern TEMPERATURE_PATTERN = Pattern.compile("(-?\\d+) ° C");
    private static final Pattern HUMIDITY_PATTERN = Pattern.compile("([\\d\\.]+)\\s+mm");

    private final RestTemplate restTemplate = new RestTemplate();

    @Cacheable(CACHE_WEATHER)
    public List<WeatherDataPoint> getWeather() {
        List<WeatherDataPoint> weatherDataPoints = new ArrayList<>(WEATHER_POINTS);
        String body = restTemplate.getForEntity("http://www.wetter.at/wetter/oesterreich/wien/favoriten/prognose/48-stunden", String.class).getBody();
        Matcher temperatureMatcher = TEMPERATURE_PATTERN.matcher(body);
        Matcher humidityMatcher = HUMIDITY_PATTERN.matcher(body);
        int hour = LocalDateTime.now().getHour();

        for (int i = 0; i < WEATHER_POINTS; i++) {
            Integer temperature = null;
            if (temperatureMatcher.find()) {
                temperature = Integer.valueOf(temperatureMatcher.group(1));
            }

            Double humidity = null;
            if (humidityMatcher.find()) {
                humidity = Double.valueOf(humidityMatcher.group(1));
            }

            int currHour = hour + i;
            weatherDataPoints.add(new WeatherDataPoint(currHour <= 24 ? currHour : currHour - 24, temperature, humidity));
        }

        return weatherDataPoints;
    }

}
