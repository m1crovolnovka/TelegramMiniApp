package com.casino.casino.controller;

import com.casino.casino.dto.request.RouletteBetRequest;
import com.casino.casino.dto.response.RouletteResultResponse;
import com.casino.casino.entity.RouletteBet;
import com.casino.casino.service.RouletteService;
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
@RequestMapping("/api/casino/roulette")
@RequiredArgsConstructor
public class RouletteController {

    private final RouletteService rouletteService;

    @PostMapping("/bet")
    public RouletteResultResponse bet(Authentication authentication, @Valid @RequestBody RouletteBetRequest body) {
        long userId = (Long) authentication.getPrincipal();
        return rouletteService.bet(userId, body);
    }

    @GetMapping("/history")
    public List<RouletteBet> history(Authentication authentication) {
        long userId = (Long) authentication.getPrincipal();
        return rouletteService.history(userId);
    }
}
