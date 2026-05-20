package com.casino.betting.service;

import com.casino.betting.dto.request.CreateBettingEventRequest;
import com.casino.betting.entity.BettingEvent;
import com.casino.betting.entity.BettingOption;
import com.casino.betting.entity.EventStatus;
import com.casino.betting.exception.BettingException;
import com.casino.betting.entity.UserBet;
import com.casino.betting.repository.BettingEventRepository;
import com.casino.betting.repository.BettingOptionRepository;
import com.casino.betting.repository.UserBetRepository;
import com.casino.economy.api.EconomyPort;
import com.casino.economy.entity.TransactionType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BetSettlementService {

    private final BettingEventRepository bettingEventRepository;
    private final BettingOptionRepository bettingOptionRepository;
    private final UserBetRepository userBetRepository;
    private final EconomyPort economyPort;

    @Transactional
    public BettingEvent createEvent(CreateBettingEventRequest req) {
        BettingEvent ev = bettingEventRepository.save(new BettingEvent(req.title(), EventStatus.ACTIVE));
        for (String label : req.optionLabels()) {
            bettingOptionRepository.save(new BettingOption(ev.getId(), label));
        }
        return ev;
    }

    @Transactional
    public void closeEvent(long eventId) {
        BettingEvent ev = bettingEventRepository.findById(eventId).orElseThrow(() -> new BettingException("Not found"));
        if (ev.getEventStatus() != EventStatus.ACTIVE) {
            throw new BettingException("Event cannot be closed");
        }
        ev.setEventStatus(EventStatus.CLOSED);
    }

    /** Fixed 2x payout on winning stakes for MVP. */
    @Transactional
    public void settle(long eventId, long winningOptionId) {
        BettingEvent ev = bettingEventRepository.findById(eventId).orElseThrow(() -> new BettingException("Not found"));
        if (ev.getEventStatus() != EventStatus.CLOSED && ev.getEventStatus() != EventStatus.ACTIVE) {
            throw new BettingException("Event cannot be settled");
        }
        List<BettingOption> options = bettingOptionRepository.findByEventId(eventId);
        for (BettingOption o : options) {
            o.setWinning(o.getId().equals(winningOptionId));
        }
        List<UserBet> winners = userBetRepository.findByBettingOptionIdAndPaidOutIsFalse(winningOptionId);
        for (UserBet b : winners) {
            long payout = b.getStakeCoins() * 2;
            economyPort.credit(
                    b.getUserId(),
                    payout,
                    "bet-payout:" + b.getId(),
                    TransactionType.BET_PAYOUT,
                    "bet_payout");
            b.setPaidOut(true);
        }
        ev.setEventStatus(EventStatus.SETTLED);
    }
}
