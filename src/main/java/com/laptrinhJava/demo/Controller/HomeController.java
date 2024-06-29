package com.laptrinhJava.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {
    public HomeController() {
    }

    @GetMapping({"/"})
    public RedirectView redirectToProducts() {
        return new RedirectView("/products");
    }
}
