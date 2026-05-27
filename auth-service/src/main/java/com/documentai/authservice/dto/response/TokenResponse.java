package com.documentai.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {

    private String username;
    private String accessToken;
    private String refreshToken;
    private final String tokenType= "Bearer";

}
