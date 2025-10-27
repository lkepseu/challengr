package com.challengr.auth.api;

import com.challengr.auth.model.User;
import com.challengr.auth.service.AuthService;
import com.challengr.auth.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    record RegisterReq(String email, String password, String handle) {}
    record LoginReq(String email, String password) {}

    // 🧾 Enregistrement d’un nouvel utilisateur
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq req) {
        log.info("➡️ [POST] User registration started (email={})", req.email());

        try {
            User user = authService.register(req.email(), req.password(), req.handle());
            String token = jwtService.generateToken(user);

            log.info("✅ User registered successfully (id={}, handle={}, email={})",
                    user.getId(), user.getHandle(), user.getEmail());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "handle", user.getHandle(),
                    "email", user.getEmail()
            ));

        } catch (Exception e) {
            log.error("❌ Registration failed for email={} : {}", req.email(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 🔐 Connexion d’un utilisateur existant
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        log.info("➡️ [POST] User login attempt (email={})", req.email());

        try {
            User user = authService.login(req.email(), req.password());
            String token = jwtService.generateToken(user);

            log.info("✅ User logged in successfully (id={}, handle={}, email={})",
                    user.getId(), user.getHandle(), user.getEmail());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "handle", user.getHandle(),
                    "email", user.getEmail()
            ));

        } catch (Exception e) {
            log.warn("⚠️ Login failed for email={} : {}", req.email(), e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authHeader) {
        log.info("➡️ [GET] /api/auth/validate called");

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("⚠️ Missing or invalid Authorization header");
                return ResponseEntity.badRequest().body("Missing Bearer token");
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            if (jwtService.isTokenValid(token, username)) {
                log.info("✅ Token valid for user={}", username);
                return ResponseEntity.ok("Token valid");
            } else {
                log.warn("❌ Invalid token for user={}", username);
                return ResponseEntity.status(401).body("Invalid token");
            }

        } catch (Exception e) {
            log.error("❌ Error during token validation: {}", e.getMessage(), e);
            return ResponseEntity.status(401).body("Token validation failed");
        }
    }

}
