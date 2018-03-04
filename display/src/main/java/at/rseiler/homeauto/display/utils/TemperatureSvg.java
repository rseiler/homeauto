package at.rseiler.homeauto.display.utils;

import at.rseiler.homeauto.display.model.WeatherDataPoint;

import java.util.List;

public class TemperatureSvg {

    private final List<WeatherDataPoint> weatherDataPoints;
    private final int max;
    private final int diff;
    private final int lineHeight;

    public TemperatureSvg(List<WeatherDataPoint> weatherDataPoints) {
        this.weatherDataPoints = weatherDataPoints;
        int min = weatherDataPoints.stream()
                .map(WeatherDataPoint::getTemperature)
                .min(Integer::compare)
                .orElse(0);
        max = weatherDataPoints.stream()
                .map(WeatherDataPoint::getTemperature)
                .max(Integer::compare)
                .orElse(0);
        diff = max - min;
        lineHeight = 250 / diff;
    }

    public String temperature() {
        StringBuilder svg = new StringBuilder(1024);
        horizontalLines(svg);
        verticalLines(svg);
        temperatureLine(svg);
        return svg.toString();
    }

    private void temperatureLine(StringBuilder svg) {
        svg.append("<path class=\"data\" d=\"M60 ");
        for (int i = 0; i < weatherDataPoints.size(); i++) {
            if (i > 0) {
                svg.append(" L ")
                        .append(60 + i * 28)
                        .append(' ');
            }

            svg.append((max - weatherDataPoints.get(i).getTemperature()) * lineHeight + 12);
        }
        svg.append("\"/>");
    }

    private void verticalLines(StringBuilder svg) {
        for (int i = 0; i < 6; i++) {
            int x = 60 + i * 28 * 3;
            int y = diff * lineHeight + 12;
            String text = String.valueOf(weatherDataPoints.get(i * 3).getHour());
            text = text.length() > 1 ? text : "0" + text;
            svg.append("<text x=\"").append(x).append("\" y=\"").append(y + 5).append("\" transform=\"rotate(90 ").append(x).append(',').append(y + 5).append(")\">").append(text).append("</text>");
            svg.append("<line x1=\"").append(x).append("\" y1=\"0\" x2=\"").append(x).append("\" y2=\"").append(y).append("\" stroke=\"silver\"/>");
        }
    }

    private void horizontalLines(StringBuilder svg) {
        for (int i = 0; i <= diff; i++) {
            int y = i * lineHeight + 12;
            String text = String.valueOf(max - i);
            text = new String(new char[3 - text.length()]).replaceAll("\0", "&#160;") + text;
            String classAttr = i == 0 || i == diff ? " class=\"bold\"" : "";
            svg.append("<text x=\"0\" y=\"").append(y + 5).append('"').append(classAttr).append(">").append(text).append("</text>");
            svg.append("<line x1=\"60\" y1=\"").append(y).append("\" x2=\"500\" y2=\"").append(y).append("\" stroke-width=\"1\" stroke=\"silver\"/>");
        }
    }
}
