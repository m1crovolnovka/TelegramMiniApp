package com.casino.casino.dto.response;

import java.util.List;

public record SlotSpinResponse(long payout, String variant, List<String> symbols) {}
