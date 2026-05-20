package com.casino.auth.controller;

import com.casino.auth.dto.request.TelegramLoginRequest;
import com.casino.auth.dto.response.TokenResponse;
import com.casino.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/telegram")
    public TokenResponse telegram(@Valid @RequestBody TelegramLoginRequest body) {
        return authService.authenticateTelegram(body.initData());
    }
}
