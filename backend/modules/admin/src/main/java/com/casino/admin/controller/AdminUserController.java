package com.casino.admin.controller;

import com.casino.admin.dto.response.AdminUserSummaryResponse;
import com.casino.admin.service.AdminUserService;
import com.casino.economy.dto.response.TransactionResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public List<AdminUserSummaryResponse> list() {
        return adminUserService.listUsers();
    }

    @GetMapping("/{userId}")
    public AdminUserSummaryResponse get(@PathVariable long userId) {
        return adminUserService.getUser(userId);
    }

    @GetMapping("/{userId}/transactions")
    public List<TransactionResponse> transactions(
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return adminUserService.userTransactions(userId, page, size);
    }
}
