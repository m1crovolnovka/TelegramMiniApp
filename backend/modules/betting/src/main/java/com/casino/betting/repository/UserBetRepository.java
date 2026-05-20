package com.casino.betting.repository;

import com.casino.betting.entity.UserBet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBetRepository extends JpaRepository<UserBet, Long> {

    List<UserBet> findByUserIdOrderByIdDesc(long userId);

    List<UserBet> findByBettingOptionId(long bettingOptionId);

    List<UserBet> findByBettingOptionIdAndPaidOutIsFalse(long bettingOptionId);
}
