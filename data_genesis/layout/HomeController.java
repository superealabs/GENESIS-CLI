package my.refuge.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class HomeController {

    @Autowired
    public HomeController() {
    }

    @GetMapping
    public String index() {
        return "redirect:/index";
    }
}
