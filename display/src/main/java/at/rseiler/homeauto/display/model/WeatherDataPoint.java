package at.rseiler.homeauto.display.model;

public class WeatherDataPoint {
    private final int hour;
    private final Integer temperature;
    private final Double humidity;

    public WeatherDataPoint(int hour, Integer temperature, Double humidity) {
        this.hour = hour;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public int getHour() {
        return hour;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    @Override
    public String toString() {
        return "WeatherDataPoint{" +
                "hour=" + hour +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                '}';
    }
}
