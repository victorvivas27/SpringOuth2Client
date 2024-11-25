package com.oauth.service;

import com.oauth.entity.User;
import com.oauth.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AWSS3Service awss3Service;


    public User userSave(User user, MultipartFile file) {
        validateUser(user);
        String fileUrl = awss3Service.uploadFileToS3(file);
        user.setPicture(fileUrl);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User userId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    public boolean existsByUsername(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private void validateUser(User user) {
        if (existsByUsername(user.getEmail())) {
            throw new IllegalArgumentException("El usuario ya existe: " + user.getEmail());
        }

        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
    }
}
