package com.laptrinhJava.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {
    public HomeController() {
    }

    @GetMapping("/home/welcome")
    public String showHomeList() {
        return "/home/welcome";
    }

    @GetMapping("/")
    public RedirectView redirectToProducts() {
        return new RedirectView("/home/welcome");
    }
}
