package com.casino.betting.service;

import com.casino.betting.dto.response.BettingEventResponse;
import com.casino.betting.entity.BettingEvent;
import com.casino.betting.entity.EventStatus;
import com.casino.betting.mapper.BettingMapper;
import com.casino.betting.repository.BettingEventRepository;
import com.casino.betting.repository.BettingOptionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BettingQueryService {

    private final BettingEventRepository eventRepository;
    private final BettingOptionRepository optionRepository;
    private final BettingMapper bettingMapper;

    @Transactional(readOnly = true)
    public List<BettingEventResponse> listOpenEvents() {
        return eventRepository.findByEventStatus(EventStatus.ACTIVE).stream()
                .map(this::mapEvent)
                .toList();
    }

    private BettingEventResponse mapEvent(BettingEvent e) {
        return bettingMapper.toEvent(e, optionRepository.findByEventId(e.getId()));
    }
}
