package com.oauth.service;

import com.oauth.entity.CustomUserDetails;
import com.oauth.interfaces.ProfileInterface;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProfileService implements ProfileInterface {
    public Map<String, Object> getUserDetails(Authentication authentication, Model model) {
        Map<String, Object> userDetails = new HashMap<>();

        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oauth2User.getAttributes();
            String name = (attributes.get("name") != null) ? attributes.get("name").toString() : "";
            String email = (attributes.get("email") != null) ? attributes.get("email").toString() : "";
            String login = (attributes.get("login") != null) ? attributes.get("login").toString() : "";
            String picture = null;
            if (attributes.containsKey("picture")) {
                picture = attributes.get("picture").toString();
            } else if (attributes.containsKey("avatar_url")) {
                picture = attributes.get("avatar_url").toString();
            }
            // Si no hay un avatar, usar la primera letra del nombre como fallback
            if (picture == null || picture.isEmpty()) {
                picture = name.isEmpty() ? "N/A" : String.valueOf(name.charAt(0)).toUpperCase();
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

        return userDetails;
    }
}
