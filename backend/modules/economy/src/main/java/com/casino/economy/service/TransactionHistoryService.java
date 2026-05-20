package com.casino.economy.service;

import com.casino.economy.dto.response.TransactionResponse;
import com.casino.economy.entity.LedgerEntry;
import com.casino.economy.mapper.TransactionMapper;
import com.casino.economy.repository.LedgerEntryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final TransactionMapper transactionMapper;

    @Transactional(readOnly = true)
    public List<TransactionResponse> history(long userId, int page, int size) {
        return ledgerEntryRepository
                .findByUserIdOrderByIdDesc(userId, PageRequest.of(page, size))
                .stream()
                .map(transactionMapper::toResponse)
                .toList();
    }
}
