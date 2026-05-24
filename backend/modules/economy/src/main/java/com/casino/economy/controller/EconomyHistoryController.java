package com.casino.economy.controller;

import com.casino.economy.dto.response.TransactionResponse;
import com.casino.economy.service.TransactionHistoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/economy")
@RequiredArgsConstructor
public class EconomyHistoryController {

    private final TransactionHistoryService transactionHistoryService;

    @GetMapping("/history")
    public List<TransactionResponse> history(
            Authentication authentication,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "50") int size) {
        long userId = (Long) authentication.getPrincipal();
        return transactionHistoryService.history(userId, page, Math.min(size, 200));
    }
}
