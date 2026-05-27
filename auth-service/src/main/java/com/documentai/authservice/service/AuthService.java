package com.documentai.authservice.service;

import com.documentai.authservice.domain.RefreshToken;
import com.documentai.authservice.domain.User;
import com.documentai.authservice.dto.response.TokenResponse;
import com.documentai.authservice.repository.RefreshTokenRepository;
import com.documentai.authservice.repository.UserRepository;
import com.documentai.authservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public void register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username is already taken.");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .tenantId(UUID.randomUUID().toString())
                .build();
        userRepository.save(user);
    }

    @Transactional
    public TokenResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password."));

        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Invalid username or password");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return TokenResponse.builder()
                .username(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenResponse refreshAccessToken(String requestRefreshToken) {
        return refreshTokenRepository.findByToken(requestRefreshToken)
                .map(jwtService::verifyRefreshTokenExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtService.generateAccessToken(user);
                    return TokenResponse
                            .builder().username(user.getUsername())
                            .accessToken(newAccessToken)
                            .refreshToken(requestRefreshToken).build();
                }).orElseThrow(() -> new RuntimeException("Refresh token is unrecognized or invalid."));
    }

}
