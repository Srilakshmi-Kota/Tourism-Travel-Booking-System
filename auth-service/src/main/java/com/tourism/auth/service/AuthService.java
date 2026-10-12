package com.tourism.auth.service;

import com.tourism.auth.model.User;
import com.tourism.auth.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository repository) { this.repository = repository; }

    public User register(String name, String email, String password, String phone) {
        if (name == null || name.isBlank() || email == null || email.isBlank() || password == null || password.length() < 6) {
            throw new IllegalArgumentException("Name, email and a password of at least 6 characters are required.");
        }
        if (repository.findByEmailIgnoreCase(email.trim()).isPresent()) throw new IllegalArgumentException("Email is already registered.");
        return repository.save(new User(name.trim(), email.trim().toLowerCase(), encoder.encode(password), phone));
    }

    public Map<String,Object> login(String email, String password) {
        User user = repository.findByEmailIgnoreCase(email == null ? "" : email.trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        if (!encoder.matches(password == null ? "" : password, user.getPasswordHash())) throw new IllegalArgumentException("Invalid email or password.");
        user.setSessionToken(UUID.randomUUID().toString());
        repository.save(user);
        return response(user);
    }

    public User validate(String token) {
        if (token == null || token.isBlank()) throw new IllegalArgumentException("Missing authentication token.");
        return repository.findBySessionToken(token).orElseThrow(() -> new IllegalArgumentException("Invalid or expired session."));
    }

    public void logout(String token) {
        User user = validate(token);
        user.setSessionToken(null);
        repository.save(user);
    }

    public Map<String,Object> response(User user) {
        return Map.of("id", user.getId(), "name", user.getName(), "email", user.getEmail(), "phone", user.getPhone() == null ? "" : user.getPhone(), "token", user.getSessionToken() == null ? "" : user.getSessionToken());
    }
}
