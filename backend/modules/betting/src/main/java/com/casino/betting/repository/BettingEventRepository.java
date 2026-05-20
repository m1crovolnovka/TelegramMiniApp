package com.casino.betting.repository;

import com.casino.betting.entity.BettingEvent;
import com.casino.betting.entity.EventStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BettingEventRepository extends JpaRepository<BettingEvent, Long> {

    List<BettingEvent> findByEventStatus(EventStatus status);
}
