package com.challengr.auth.service;

import com.challengr.auth.model.User;
import com.challengr.auth.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepo userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    // 🧾 Inscription
    public User register(String email, String password, String handle) {
        log.info("[SERVICE] Registering new user (email={}, handle={})", email, handle);

        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("⚠️ Registration blocked: email already in use ({})", email);
            throw new RuntimeException("Email déjà utilisé.");
        }

        User user = new User();
        user.setEmail(email);
        user.setHandle(handle == null || handle.isBlank() ? email.split("@")[0] : handle);
        user.setPasswordHash(encoder.encode(password));

        User saved = userRepository.save(user);
        log.info("✅ User saved successfully (id={}, handle={}, email={})",
                saved.getId(), saved.getHandle(), saved.getEmail());
        return saved;
    }

    // 🔐 Connexion
    public User login(String email, String password) {
        log.info("[SERVICE] User login verification (email={})", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé."));

        if (!encoder.matches(password, user.getPasswordHash())) {
            log.warn("⚠️ Incorrect password for email={}", email);
            throw new RuntimeException("Mot de passe incorrect.");
        }

        log.info("✅ Login success (id={}, handle={}, email={})",
                user.getId(), user.getHandle(), user.getEmail());
        return user;
    }
}
