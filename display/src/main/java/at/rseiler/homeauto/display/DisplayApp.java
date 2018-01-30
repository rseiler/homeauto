package at.rseiler.homeauto.display;

import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class DisplayApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisplayApp.class);
    public static final String CACHE_OWA = "cacheOwa";
    public static final String CACHE_WEATHER = "cacheWeather";

    public static void main(String[] args) {
        SpringApplication.run(DisplayApp.class, args);
    }

    @Bean(CACHE_OWA)
    public Cache cacheOwa() {
        return new GuavaCache(CACHE_OWA, CacheBuilder.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build());
    }

    @Bean(CACHE_WEATHER)
    public Cache cacheWeather() {
        return new GuavaCache(CACHE_WEATHER, CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build());
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void invalidateCacheOWA() {
        LOGGER.info("invalidate owa cache");
        cacheOwa().clear();
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void invalidateCacheWeather() {
        LOGGER.info("invalidate weather cache");
        cacheWeather().clear();
    }
}
