package at.rseiler.homeauto.milightweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Listens to HTTP requests and returns the website.
 */
@Controller
public class WebController {

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }
}
