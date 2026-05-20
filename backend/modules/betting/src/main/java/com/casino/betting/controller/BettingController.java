package com.casino.betting.controller;

import com.casino.betting.dto.request.PlaceBetRequest;
import com.casino.betting.dto.response.BettingEventResponse;
import com.casino.betting.entity.UserBet;
import com.casino.betting.repository.UserBetRepository;
import com.casino.betting.service.BettingQueryService;
import com.casino.betting.service.BettingService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/betting")
@RequiredArgsConstructor
public class BettingController {

    private final BettingQueryService bettingQueryService;
    private final BettingService bettingService;
    private final UserBetRepository userBetRepository;

    @GetMapping("/events")
    public List<BettingEventResponse> events() {
        return bettingQueryService.listOpenEvents();
    }

    @PostMapping("/place")
    public long place(Authentication authentication, @Valid @RequestBody PlaceBetRequest body) {
        long userId = (Long) authentication.getPrincipal();
        return bettingService.placeBet(userId, body.optionId(), body.stakeCoins());
    }

    @GetMapping("/history")
    public List<UserBet> history(Authentication authentication) {
        long userId = (Long) authentication.getPrincipal();
        return userBetRepository.findByUserIdOrderByIdDesc(userId);
    }
}
