package com.casino.users.dto.response;

public record LeaderboardEntryResponse(
        int rank, long userId, String username, long balanceCoins, long uniqueStudentsOwned) {}
