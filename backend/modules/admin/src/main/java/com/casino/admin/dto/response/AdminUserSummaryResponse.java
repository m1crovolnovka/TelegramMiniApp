package com.casino.admin.dto.response;

public record AdminUserSummaryResponse(
        long id, String username, long telegramId, long balanceCoins, long uniqueStudentsOwned) {}
