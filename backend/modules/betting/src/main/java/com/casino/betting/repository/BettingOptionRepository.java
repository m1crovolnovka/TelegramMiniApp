package com.casino.betting.repository;

import com.casino.betting.entity.BettingOption;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BettingOptionRepository extends JpaRepository<BettingOption, Long> {

    List<BettingOption> findByEventId(long eventId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from BettingOption o where o.id = :id")
    Optional<BettingOption> lockById(@Param("id") long id);
}
