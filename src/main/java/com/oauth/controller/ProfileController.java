package com.oauth.controller;

import com.oauth.service.ProfileService;
import com.oauth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@AllArgsConstructor
public class ProfileController {
    private final UserService userService;
    private final ProfileService profileService;

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        Map<String, Object> userDetails = profileService.getUserDetails(authentication, model);
        if (!userDetails.isEmpty()) {
            model.addAllAttributes(userDetails);
        } else {
            model.addAttribute("error", "Usuario no encontrado.");
        }

        return "profile";
    }
}




