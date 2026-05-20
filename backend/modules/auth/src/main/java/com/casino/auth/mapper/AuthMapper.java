package com.casino.auth.mapper;

import com.casino.auth.dto.response.TokenResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public TokenResponse toToken(String accessToken) {
        return new TokenResponse(accessToken);
    }
}
