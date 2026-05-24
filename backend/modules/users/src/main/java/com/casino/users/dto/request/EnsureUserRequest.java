package com.casino.users.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EnsureUserRequest(@NotBlank String username, Long telegramId) {}
