package at.rseiler.homeauto.display.controller;

import at.rseiler.homeauto.display.model.CalendarEntry;
import at.rseiler.homeauto.display.model.WeatherDataPoint;
import at.rseiler.homeauto.display.service.OwaService;
import at.rseiler.homeauto.display.service.WeatherService;
import at.rseiler.homeauto.display.utils.HumiditySvg;
import at.rseiler.homeauto.display.utils.TemperatureSvg;
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
        String svg = new HumiditySvg(weatherDataPoints).humidity();
        modelAndView.addObject("humiditySvg", svg);
    }

    private void addTemperatureSvg(ModelAndView modelAndView, List<WeatherDataPoint> weatherDataPoints) {
        String svg = new TemperatureSvg(weatherDataPoints).temperature();
        modelAndView.addObject("temperatureSvg", svg);
    }

}
