package com.example.demo.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtTokenUtils {
    private final SecretKey secretKey;

    @Value("${token.jwt.lifetime}")
    private Duration jwtLifetime;

    @Value("${token.refresh.lifetime}")
    private Duration refreshLifetime;

    public JwtTokenUtils(@Value("${token.jwt.secret}") String secret) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    //UserDetails на случай надобности в будущем
    public String generateToken(UserDetails userDetails, String email) {
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + jwtLifetime.toMillis());
        return Jwts.builder()
                .subject(email)
                .issuedAt(issuedDate)
                .expiration(expiredDate)
                .signWith(secretKey)
                .compact();
    }

    public String getEmail(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public Claims getAllClaimsFromToken(String jws) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jws)
                .getPayload();
    }

    public Collection<? extends GrantedAuthority> getRoles(String jwt) {
        return getAllClaimsFromToken(jwt).get("roles", Collection.class);
    }
}
