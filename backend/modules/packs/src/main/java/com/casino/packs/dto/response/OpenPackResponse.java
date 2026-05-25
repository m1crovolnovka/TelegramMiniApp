package com.casino.packs.dto.response;

import java.util.List;

public record OpenPackResponse(
        String packKind, DroppedCardResponse droppedCard, List<DroppedCardResponse> droppedCards) {}
