package com.casino.admin.controller;

import com.casino.betting.dto.request.CreateBettingEventRequest;
import com.casino.betting.dto.request.SettleBettingEventRequest;
import com.casino.betting.dto.response.BettingEventResponse;
import com.casino.betting.entity.BettingEvent;
import com.casino.betting.service.BetSettlementService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/betting")
@RequiredArgsConstructor
public class AdminBettingController {

    private final BetSettlementService betSettlementService;

    @GetMapping("/events")
    public List<BettingEventResponse> listEvents() {
        return betSettlementService.listAllEvents();
    }

    @PostMapping("/events")
    public BettingEvent create(@Valid @RequestBody CreateBettingEventRequest body) {
        return betSettlementService.createEvent(body);
    }

    @PostMapping("/events/{eventId}/close")
    public void close(@PathVariable("eventId") long eventId) {
        betSettlementService.closeEvent(eventId);
    }

    @PostMapping("/events/{eventId}/settle")
    public void settle(@PathVariable("eventId") long eventId, @Valid @RequestBody SettleBettingEventRequest body) {
        betSettlementService.settle(eventId, body.winningOptionId());
    }
}
