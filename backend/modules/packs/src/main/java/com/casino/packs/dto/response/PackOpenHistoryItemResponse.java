package com.casino.packs.dto.response;

import java.time.Instant;

public record PackOpenHistoryItemResponse(
        long id, long packId, DroppedCardResponse droppedCard, Instant openedAt) {}
