package at.rseiler.homeauto.display.utils;

import at.rseiler.homeauto.display.model.WeatherDataPoint;

import java.util.List;

public class HumiditySvg {

    private final List<WeatherDataPoint> weatherDataPoints;
    private final double max;
    private final double diff;
    private final int lineHeight;

    public HumiditySvg(List<WeatherDataPoint> weatherDataPoints) {
        this.weatherDataPoints = weatherDataPoints;
        double maxElement = weatherDataPoints.stream()
                .map(WeatherDataPoint::getHumidity)
                .max(Double::compare)
                .orElse(0d);
        max = maxElement > 0.6 ? maxElement : 0.6;
        diff = max / 6;
        lineHeight = 250 / 6;
    }

    public String humidity() {
        StringBuilder svg = new StringBuilder(1024);
        if (max > 0.0) {
            horizontalLines(svg);
            verticalLines(svg);
            humidityLine(svg);
        } else {
            svg.append("<text class=\"no-rain\" x=\"50%\" y=\"50%\" alignment-baseline=\"middle\" text-anchor=\"middle\">No rain!</text>");
        }
        return svg.toString();
    }

    private void horizontalLines(StringBuilder svg) {
        for (int i = 0; i <= 6; i++) {
            int y = i * lineHeight + 12;
            String text = String.format("%.1f", (6 - i) * diff);
            text = text.length() > 3 ? text : "&#160;" + text;
            String classAttr = i == 0 || i == 6 ? " class=\"bold\"" : "";
            svg.append("<text x=\"0\" y=\"").append(y + 5).append('"').append(classAttr).append('>').append(text).append("</text>");
            svg.append("<line x1=\"60\" y1=\"").append(y).append("\" x2=\"500\" y2=\"").append(y).append("\" stroke-width=\"1\" stroke=\"silver\"/>");
        }
    }

    private void verticalLines(StringBuilder svg) {
        for (int i = 0; i < 6; i++) {
            int x = 60 + i * 28 * 3;
            int y = lineHeight * 6 + 12;
            String text = String.valueOf(weatherDataPoints.get(i * 3).getHour());
            text = text.length() > 1 ? text : "0" + text;
            svg.append("<text x=\"").append(x).append("\" y=\"").append(y + 5).append("\" transform=\"rotate(90 ").append(x).append(',').append(y + 5).append(")\">").append(text).append("</text>");
            svg.append("<line x1=\"").append(x).append("\" y1=\"0\" x2=\"").append(x).append("\" y2=\"").append(y).append("\" stroke=\"silver\"/>");
        }
    }

    private void humidityLine(StringBuilder svg) {
        svg.append("<path class=\"data\" d=\"M60 ");
        for (int i = 0; i < weatherDataPoints.size(); i++) {
            if (i > 0) {
                svg.append(" L ")
                        .append(60 + i * 28)
                        .append(' ');
            }

            svg.append((max - weatherDataPoints.get(i).getHumidity()) / diff * lineHeight + 12);
        }
        svg.append("\"/>");
    }
}
