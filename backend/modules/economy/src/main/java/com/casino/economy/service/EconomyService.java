package com.casino.economy.service;

import com.casino.economy.api.EconomyPort;
import com.casino.economy.entity.LedgerEntry;
import com.casino.economy.entity.TransactionType;
import com.casino.economy.entity.Wallet;
import com.casino.economy.exception.InsufficientFundsException;
import com.casino.economy.exception.WalletNotFoundException;
import com.casino.economy.repository.LedgerEntryRepository;
import com.casino.economy.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EconomyService implements EconomyPort {

    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    @Value("${casino.dev.starter-coins:5000}")
    private long devStarterCoins;

    @Value("${casino.dev.starter-coins-enabled:true}")
    private boolean devStarterCoinsEnabled;

    @Override
    @Transactional
    public void ensureWallet(long userId) {
        walletRepository
                .findByUserId(userId)
                .orElseGet(
                        () -> {
                            long initial = devStarterCoinsEnabled ? devStarterCoins : 0L;
                            return walletRepository.save(new Wallet(userId, initial));
                        });
    }

    @Override
    @Transactional(readOnly = true)
    public long getBalance(long userId) {
        return walletRepository.findByUserId(userId).map(Wallet::getBalance).orElse(0L);
    }

    @Override
    @Transactional
    public void debit(long userId, long amount, String operationId, TransactionType type, String reason) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount");
        }
        if (ledgerEntryRepository.existsByUserIdAndOperationId(userId, operationId)) {
            return;
        }
        Wallet wallet =
                walletRepository.lockByUserId(userId).orElseThrow(WalletNotFoundException::new);
        if (wallet.getBalance() < amount) {
            throw new InsufficientFundsException();
        }
        wallet.setBalance(wallet.getBalance() - amount);
        ledgerEntryRepository.save(new LedgerEntry(userId, -amount, operationId, type, reason));
    }

    @Override
    @Transactional
    public void credit(long userId, long amount, String operationId, TransactionType type, String reason) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount");
        }
        if (ledgerEntryRepository.existsByUserIdAndOperationId(userId, operationId)) {
            return;
        }
        Wallet wallet =
                walletRepository.lockByUserId(userId).orElseThrow(WalletNotFoundException::new);
        wallet.setBalance(wallet.getBalance() + amount);
        ledgerEntryRepository.save(new LedgerEntry(userId, amount, operationId, type, reason));
    }

    @Override
    @Transactional
    public void transfer(long fromUserId, long toUserId, long amount, String transferOperationId, String reason) {
        if (fromUserId == toUserId) {
            throw new IllegalArgumentException("fromUserId and toUserId must differ");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount");
        }
        long first = Math.min(fromUserId, toUserId);
        long second = Math.max(fromUserId, toUserId);
        walletRepository.lockByUserId(first).orElseThrow(WalletNotFoundException::new);
        walletRepository.lockByUserId(second).orElseThrow(WalletNotFoundException::new);
        debit(fromUserId, amount, transferOperationId + ":debit", TransactionType.TRANSFER, reason);
        credit(toUserId, amount, transferOperationId + ":credit", TransactionType.TRANSFER, reason);
    }
}
