package com.casino.admin.service;

import com.casino.admin.dto.response.AdminUserSummaryResponse;
import com.casino.economy.api.EconomyPort;
import com.casino.economy.dto.response.TransactionResponse;
import com.casino.economy.service.TransactionHistoryService;
import com.casino.users.entity.User;
import com.casino.users.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final EconomyPort economyPort;
    private final TransactionHistoryService transactionHistoryService;

    @Transactional(readOnly = true)
    public List<AdminUserSummaryResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(
                        u ->
                                new AdminUserSummaryResponse(
                                        u.getId(),
                                        u.getUsername(),
                                        u.getTelegramId(),
                                        economyPort.getBalance(u.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminUserSummaryResponse getUser(long userId) {
        User u = userRepository.findById(userId).orElseThrow();
        return new AdminUserSummaryResponse(
                u.getId(), u.getUsername(), u.getTelegramId(), economyPort.getBalance(u.getId()));
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> userTransactions(long userId, int page, int size) {
        return transactionHistoryService.history(userId, page, size);
    }
}
