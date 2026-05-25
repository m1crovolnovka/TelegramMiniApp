package com.casino.betting.mapper;

import com.casino.betting.dto.response.BettingEventResponse;
import com.casino.betting.dto.response.BettingOptionResponse;
import com.casino.betting.entity.BettingEvent;
import com.casino.betting.entity.BettingOption;
import com.casino.betting.service.BettingOddsCalculator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BettingMapper {

    public BettingOptionResponse toOption(BettingOption o, long totalStakeCoins, int optionCount) {
        double coefficient =
                BettingOddsCalculator.coefficient(o.getTotalStakeCoins(), totalStakeCoins, optionCount);
        return new BettingOptionResponse(
                o.getId(), o.getLabel(), o.getTotalStakeCoins(), o.isWinning(), coefficient);
    }

    public BettingEventResponse toEvent(BettingEvent e, List<BettingOption> options) {
        long totalStake = options.stream().mapToLong(BettingOption::getTotalStakeCoins).sum();
        int count = options.size();
        return new BettingEventResponse(
                e.getId(),
                e.getTitle(),
                e.getEventStatus(),
                options.stream().map(o -> toOption(o, totalStake, count)).toList());
    }
}
