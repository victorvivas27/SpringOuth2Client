package com.oauth.interfaces;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.util.Map;

public interface ProfileInterface {
    public Map<String, Object> getUserDetails(Authentication authentication, Model model);
}
