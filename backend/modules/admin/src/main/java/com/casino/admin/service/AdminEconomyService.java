package com.casino.admin.service;

import com.casino.admin.dto.request.AdminBalanceChangeRequest;
import com.casino.economy.api.EconomyPort;
import com.casino.economy.entity.TransactionType;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminEconomyService {

    private final EconomyPort economyPort;

    @Transactional
    public void addCoins(AdminBalanceChangeRequest req) {
        economyPort.ensureWallet(req.userId());
        String op = "admin-add:" + UUID.randomUUID();
        economyPort.credit(req.userId(), req.amount(), op, TransactionType.ADMIN_ADJUST, req.reason());
    }

    @Transactional
    public void removeCoins(AdminBalanceChangeRequest req) {
        String op = "admin-remove:" + UUID.randomUUID();
        economyPort.debit(req.userId(), req.amount(), op, TransactionType.ADMIN_ADJUST, req.reason());
    }
}
