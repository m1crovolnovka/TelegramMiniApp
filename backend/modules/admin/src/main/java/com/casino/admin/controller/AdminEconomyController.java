package com.casino.admin.controller;

import com.casino.admin.dto.request.AdminBalanceChangeRequest;
import com.casino.admin.service.AdminEconomyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/economy")
@RequiredArgsConstructor
public class AdminEconomyController {

    private final AdminEconomyService adminEconomyService;

    @PostMapping("/add")
    public void add(@Valid @RequestBody AdminBalanceChangeRequest body) {
        adminEconomyService.addCoins(body);
    }

    @PostMapping("/remove")
    public void remove(@Valid @RequestBody AdminBalanceChangeRequest body) {
        adminEconomyService.removeCoins(body);
    }
}
