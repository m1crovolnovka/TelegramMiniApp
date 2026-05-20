package com.casino.economy.repository;

import com.casino.economy.entity.LedgerEntry;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    boolean existsByUserIdAndOperationId(long userId, String operationId);

    List<LedgerEntry> findByUserIdOrderByIdDesc(long userId, Pageable pageable);
}
