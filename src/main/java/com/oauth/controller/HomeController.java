package com.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@CrossOrigin
public class HomeController {
    @GetMapping("/signin")
    public String login() {
        return "signin";
    }

    @GetMapping("/fotos")
    public String photos() {
        return "fotos";
    }

    @GetMapping("/videos")
    public String videos(Model model) {
        model.addAttribute("videos", "Lista de videos disponibles");
        return "videos";
    }

    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }
}
