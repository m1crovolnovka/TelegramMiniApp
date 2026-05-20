package com.casino.users.dto.response;

import com.casino.users.entity.UserRole;

public record UserResponse(long id, long telegramId, String username, UserRole role, long balanceCoins) {}
