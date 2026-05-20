package com.casino.betting.service;

import com.casino.betting.entity.BettingEvent;
import com.casino.betting.entity.BettingOption;
import com.casino.betting.entity.EventStatus;
import com.casino.betting.entity.UserBet;
import com.casino.betting.exception.BettingException;
import com.casino.betting.repository.BettingEventRepository;
import com.casino.betting.repository.BettingOptionRepository;
import com.casino.betting.repository.UserBetRepository;
import com.casino.economy.api.EconomyPort;
import com.casino.economy.entity.TransactionType;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BettingService {

    private final BettingEventRepository bettingEventRepository;
    private final BettingOptionRepository bettingOptionRepository;
    private final UserBetRepository userBetRepository;
    private final EconomyPort economyPort;

    @Transactional
    public long placeBet(long userId, long optionId, long stakeCoins) {
        BettingOption option =
                bettingOptionRepository.lockById(optionId).orElseThrow(() -> new BettingException("Option not found"));
        BettingEvent event = bettingEventRepository.findById(option.getEventId()).orElseThrow();
        if (event.getEventStatus() != EventStatus.ACTIVE) {
            throw new BettingException("Event is not open for betting");
        }
        String opId = "bet-stake:" + UUID.randomUUID();
        economyPort.debit(userId, stakeCoins, opId, TransactionType.BET_STAKE, "bet_stake");
        UserBet bet = userBetRepository.save(new UserBet(userId, optionId, stakeCoins, opId));
        option.setTotalStakeCoins(option.getTotalStakeCoins() + stakeCoins);
        return bet.getId();
    }
}
