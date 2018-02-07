package at.rseiler.homeauto.display.controller;

import at.rseiler.homeauto.display.model.CalendarEntry;
import at.rseiler.homeauto.display.model.WeatherDataPoint;
import at.rseiler.homeauto.display.service.OwaService;
import at.rseiler.homeauto.display.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/d")
public class DisplayController {

    private final OwaService owaService;
    private final WeatherService weatherService;

    @Autowired
    public DisplayController(OwaService owaService, WeatherService weatherService) {
        this.owaService = owaService;
        this.weatherService = weatherService;
    }

    @GetMapping("/")
    public ModelAndView home() throws IOException {
        LocalDate date = LocalDate.now().plusDays(LocalTime.now().getHour() > 17 ? 1 : 0);
        List<CalendarEntry> calendarEntries = owaService.getCalendarData().stream()
                .filter(calendarEntry -> date.compareTo(calendarEntry.getFrom().toLocalDate()) >= 0 && (date.compareTo(calendarEntry.getTo().toLocalDate()) <= 0))
                .collect(Collectors.toList());
        ModelAndView modelAndView = new ModelAndView("page/home");
        modelAndView.addObject("calendarEntries", calendarEntries);
        addWeather(modelAndView);
        return modelAndView;
    }

    @GetMapping("/fragment/{fragment}")
    public ModelAndView weather(@PathVariable String fragment) throws IOException {
        ModelAndView modelAndView = home();
        modelAndView.setViewName(modelAndView.getViewName() + " :: " + fragment);
        return modelAndView;
    }

    private void addWeather(ModelAndView modelAndView) {
        List<WeatherDataPoint> weatherDataPoints = weatherService.getWeather();
        addTemperatureSvg(modelAndView, weatherDataPoints);
        addHumiditySvg(modelAndView, weatherDataPoints);
    }

    private void addHumiditySvg(ModelAndView modelAndView, List<WeatherDataPoint> weatherDataPoints) {
        double max = weatherDataPoints.stream().map(WeatherDataPoint::getHumidity).max(Double::compare).get();
        StringBuilder svg = new StringBuilder();

        if (max > 0.0) {
            max = max > 0.6 ? max : 0.6;
            double diff = max / 6;
            int lineHeight = 250 / 6;

            for (int i = 0; i <= 6; i++) {
                int y = i * lineHeight + 12;
                String text = String.format("%.1f", (6 - i) * diff);
                text = text.length() > 3 ? text : "&#160;" + text;
                String classAttr = i == 0 || i == 6 ? " class=\"bold\"" : "";
                svg.append("<text x=\"0\" y=\"").append(y + 5).append("\"").append(classAttr).append(">").append(text).append("</text>");
                svg.append("<line x1=\"60\" y1=\"").append(y).append("\" x2=\"500\" y2=\"").append(y).append("\" stroke-width=\"1\" stroke=\"silver\"/>");
            }

            for (int i = 0; i < 6; i++) {
                int x = 60 + i * 28 * 3;
                int y = lineHeight * 6 + 12;
                String text = String.valueOf(weatherDataPoints.get(i * 3).getHour());
                text = text.length() > 1 ? text : "0" + text;
                svg.append("<text x=\"").append(x).append("\" y=\"").append(y + 5).append("\" transform=\"rotate(90 ").append(x).append(",").append(y + 5).append(")\">").append(text).append("</text>");
                svg.append("<line x1=\"").append(x).append("\" y1=\"0\" x2=\"").append(x).append("\" y2=\"").append(y).append("\" stroke=\"silver\"/>");
            }

            svg.append("<path class=\"data\" d=\"M60 ");
            for (int i = 0; i < weatherDataPoints.size(); i++) {
                if (i > 0) {
                    svg.append(" L ")
                            .append(60 + i * 28)
                            .append(" ");
                }

                svg.append((max - weatherDataPoints.get(i).getHumidity()) / diff * lineHeight + 12);
            }
            svg.append("\"/>");
        } else {
            svg.append("<text class=\"no-rain\" x=\"50%\" y=\"50%\" alignment-baseline=\"middle\" text-anchor=\"middle\">No rain!</text>");
        }

        modelAndView.addObject("humiditySvg", svg.toString());
    }

    private void addTemperatureSvg(ModelAndView modelAndView, List<WeatherDataPoint> weatherDataPoints) {
        int min = weatherDataPoints.stream().map(WeatherDataPoint::getTemperature).min(Integer::compare).get();
        int max = weatherDataPoints.stream().map(WeatherDataPoint::getTemperature).max(Integer::compare).get();
        int diff = max - min;
        int lineHeight = 250 / diff;


        StringBuilder svg = new StringBuilder();

        for (int i = 0; i <= diff; i++) {
            int y = i * lineHeight + 12;
            String text = String.valueOf(max - i);
            text = new String(new char[3 - text.length()]).replaceAll("\0", "&#160;") + text;
            String classAttr = i == 0 || i == diff ? " class=\"bold\"" : "";
            svg.append("<text x=\"0\" y=\"").append(y + 5).append("\"").append(classAttr).append(">").append(text).append("</text>");
            svg.append("<line x1=\"60\" y1=\"").append(y).append("\" x2=\"500\" y2=\"").append(y).append("\" stroke-width=\"1\" stroke=\"silver\"/>");
        }

        for (int i = 0; i < 6; i++) {
            int x = 60 + i * 28 * 3;
            int y = diff * lineHeight + 12;
            String text = String.valueOf(weatherDataPoints.get(i * 3).getHour());
            text = text.length() > 1 ? text : "0" + text;
            svg.append("<text x=\"").append(x).append("\" y=\"").append(y + 5).append("\" transform=\"rotate(90 ").append(x).append(",").append(y + 5).append(")\">").append(text).append("</text>");
            svg.append("<line x1=\"").append(x).append("\" y1=\"0\" x2=\"").append(x).append("\" y2=\"").append(y).append("\" stroke=\"silver\"/>");
        }

        svg.append("<path class=\"data\" d=\"M60 ");
        for (int i = 0; i < weatherDataPoints.size(); i++) {
            if (i > 0) {
                svg.append(" L ")
                        .append(60 + i * 28)
                        .append(" ");
            }

            svg.append((max - weatherDataPoints.get(i).getTemperature()) * lineHeight + 12);
        }
        svg.append("\"/>");

        modelAndView.addObject("temperatureSvg", svg.toString());
    }
}
