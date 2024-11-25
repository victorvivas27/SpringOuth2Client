package com.oauth.controller;

import com.oauth.entity.User;
import com.oauth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
@AllArgsConstructor
@CrossOrigin
public class RegistrationController {
    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @ModelAttribute User user,
            @RequestPart(value = "file") MultipartFile file) {
        try {
            User savedUser = userService.userSave(user, file);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuario registrado correctamente con ID: " + savedUser.getId());
            response.put("fileUrl", savedUser.getPicture());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.userId(id);
        user.setPassword(null);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
