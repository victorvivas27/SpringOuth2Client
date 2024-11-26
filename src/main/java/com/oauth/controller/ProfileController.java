package com.oauth.controller;

import com.oauth.entity.CustomUserDetails;
import com.oauth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@AllArgsConstructor
public class ProfileController {
    private final UserService userService;

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            //System.out.println("Atributos del usuario autenticado en GitHub:");
            //oauth2User.getAttributes().forEach((key, value) -> System.out.println(key + ": " + value));
            Map<String, Object> attributes = oauth2User.getAttributes();
            String name = (attributes.get("name") != null) ? attributes.get("name").toString() : "Nombre no disponible";
            String email = (attributes.get("email") != null) ? attributes.get("email").toString() : "";
            String login = (attributes.get("login") != null) ? attributes.get("login").toString() : "";
            String picture = null;
            if (attributes.containsKey("picture")) {
                picture = attributes.get("picture").toString();
            } else if (attributes.containsKey("avatar_url")) {
                picture = attributes.get("avatar_url").toString();
            }
            if (picture == null) {
                picture = "Imagen no disponible";
            }
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("login", login);
            model.addAttribute("picture", picture);
        } else if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            String email = user.getUsername();
            String name = user.getName();
            String picture = user.getPicture();
            Long id = user.getId();
            if (user != null) {
                model.addAttribute("name", name);
                model.addAttribute("email", email);
                model.addAttribute("login", null);
                model.addAttribute("picture", picture);
            } else {
                model.addAttribute("error", "Usuario no encontrado.");
            }
        }

        return "profile";
    }
}

