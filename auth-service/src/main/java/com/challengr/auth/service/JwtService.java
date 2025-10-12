package com.challengr.auth;
import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import java.security.Key; import java.util.*;
import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final Key key; private final long exp;
    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expirationMillis}") long exp){
        this.key = Keys.hmacShaKeyFor(secret.getBytes()); this.exp = exp;
    }
    public String generate(Long userId, String email){
        Date now=new Date(), then=new Date(now.getTime()+exp);
        return Jwts.builder().setSubject(String.valueOf(userId))
                .claim("email",email).setIssuedAt(now).setExpiration(then)
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }
    public Jws<Claims> parse(String jwt){ return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt); }
}
