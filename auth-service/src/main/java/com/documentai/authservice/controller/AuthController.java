package com.documentai.authservice.controller;

import com.documentai.authservice.dto.request.AuthRequest;
import com.documentai.authservice.dto.request.RefreshRequest;
import com.documentai.authservice.dto.response.TokenResponse;
import com.documentai.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest authRequest) {
        authService.register(authRequest.getUsername(), authRequest.getPassword());
        return new ResponseEntity<>(
                "User registered successfully under a unique tenant context.", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody AuthRequest request) {
        TokenResponse tokenResponse = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(tokenResponse);
    }

    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest request) {
        TokenResponse tokenResponse = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(tokenResponse);
    }

}
