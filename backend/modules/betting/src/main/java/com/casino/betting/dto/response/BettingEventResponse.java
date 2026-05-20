package com.casino.betting.dto.response;

import com.casino.betting.entity.EventStatus;
import java.util.List;

public record BettingEventResponse(long id, String title, EventStatus status, List<BettingOptionResponse> options) {}
