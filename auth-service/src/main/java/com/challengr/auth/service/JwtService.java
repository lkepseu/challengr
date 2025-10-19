package com.challengr.auth.service;

import com.challengr.auth.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.util.function.Function;
import io.jsonwebtoken.Claims;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY = "MySuperSecretKeyForJwtEncoding123456";

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("handle", user.getHandle())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 jour
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // ✅ Extraire le username (souvent l’email)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ Vérifie si le token est valide pour un utilisateur donné
    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // 🔍 Récupère une donnée spécifique (claim) depuis le token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ⏰ Vérifie si le token est expiré
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ⏰ Récupère la date d’expiration
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 🧩 Parse tous les claims à partir du token
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 🔑 Crée la clé HMAC à partir de la clé secrète encodée en Base64
    private Key getSignInKey() {
        byte[] keyBytes = Base64.getEncoder().encode(SECRET_KEY.getBytes());
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
    }
}
