package com.casino.cards.dto.response;

import java.util.List;

public record InventoryResponse(List<InventoryItemResponse> items, int totalQuantity) {}
