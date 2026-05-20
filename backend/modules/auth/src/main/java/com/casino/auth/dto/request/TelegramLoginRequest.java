package com.casino.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TelegramLoginRequest(@NotBlank String initData) {}
