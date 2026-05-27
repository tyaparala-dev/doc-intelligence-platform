package com.documentai.authservice.security;

import com.documentai.authservice.domain.RefreshToken;
import com.documentai.authservice.domain.User;
import com.documentai.authservice.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${JWT_SECRET_KEY}")
    private String SECRET_KEY;

    @Value("${JWT_ACCESS_TOKEN_EXPIRY}")
    private Long accessTokenExpiry;

    @Value("${JWT_REFRESH_TOKEN_EXPIRATION_DAYS}")
    private Long refreshTokenExpiry;

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tenantId", user.getTenantId());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+accessTokenExpiry))
                .signWith(getSigningKey())
                .compact();
    }

    @Transactional
    public String generateRefreshToken(User user) {
        refreshTokenRepository.deleteByUserId(user.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.from(LocalDateTime.now().plusDays(refreshTokenExpiry)))
                .build();
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public RefreshToken verifyRefreshTokenExpiration(RefreshToken token){
        if (token.getExpiryDate().isBefore(Instant.from(LocalDateTime.now()))){
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired. Please log in again.");
        }
        return token;
    }

    public String getUsername(String token){
        return getAllClaims(token).getSubject();
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build().parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

}
