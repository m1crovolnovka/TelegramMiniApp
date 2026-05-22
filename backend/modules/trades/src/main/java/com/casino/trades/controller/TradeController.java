package com.casino.trades.controller;

import com.casino.trades.dto.request.AddTradeItemRequest;
import com.casino.trades.dto.request.CreateTradeRequest;
import com.casino.trades.dto.response.TradeResponse;
import com.casino.trades.service.TradeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping
    public TradeResponse create(Authentication authentication, @Valid @RequestBody CreateTradeRequest body) {
        long userId = (Long) authentication.getPrincipal();
        return tradeService.create(userId, body);
    }

    @PostMapping("/{tradeId}/items")
    public TradeResponse addItem(
            Authentication authentication,
            @PathVariable long tradeId,
            @Valid @RequestBody AddTradeItemRequest body) {
        long userId = (Long) authentication.getPrincipal();
        return tradeService.addItem(userId, tradeId, body);
    }

    @PostMapping("/{tradeId}/send")
    public TradeResponse send(Authentication authentication, @PathVariable long tradeId) {
        long userId = (Long) authentication.getPrincipal();
        return tradeService.send(userId, tradeId);
    }

    @PostMapping("/{tradeId}/accept")
    public TradeResponse accept(Authentication authentication, @PathVariable long tradeId) {
        long userId = (Long) authentication.getPrincipal();
        return tradeService.accept(userId, tradeId);
    }

    @PostMapping("/{tradeId}/reject")
    public void reject(Authentication authentication, @PathVariable long tradeId) {
        long userId = (Long) authentication.getPrincipal();
        tradeService.reject(userId, tradeId);
    }

    @GetMapping("/history")
    public List<TradeResponse> history(Authentication authentication) {
        long userId = (Long) authentication.getPrincipal();
        return tradeService.history(userId);
    }

    @GetMapping("/{tradeId}")
    public TradeResponse get(Authentication authentication, @PathVariable long tradeId) {
        long userId = (Long) authentication.getPrincipal();
        return tradeService.get(userId, tradeId);
    }
}
