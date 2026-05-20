package com.casino.trades.repository;

import com.casino.trades.entity.Trade;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Trade t where t.id = :id")
    Optional<Trade> lockById(@Param("id") long id);

    List<Trade> findByInitiatorUserIdOrPartnerUserIdOrderByIdDesc(long initiatorUserId, long partnerUserId);
}
