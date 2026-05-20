package com.casino.betting.mapper;

import com.casino.betting.dto.response.BettingEventResponse;
import com.casino.betting.dto.response.BettingOptionResponse;
import com.casino.betting.entity.BettingEvent;
import com.casino.betting.entity.BettingOption;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BettingMapper {

    public BettingOptionResponse toOption(BettingOption o) {
        return new BettingOptionResponse(o.getId(), o.getLabel(), o.getTotalStakeCoins(), o.isWinning());
    }

    public BettingEventResponse toEvent(BettingEvent e, List<BettingOption> options) {
        return new BettingEventResponse(
                e.getId(), e.getTitle(), e.getEventStatus(), options.stream().map(this::toOption).toList());
    }
}
