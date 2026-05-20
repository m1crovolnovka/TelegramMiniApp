package com.casino.packs.service;

import com.casino.packs.dto.response.PackOpenHistoryItemResponse;
import com.casino.packs.dto.response.PackResponse;
import com.casino.packs.entity.PackOpenHistory;
import com.casino.packs.mapper.PackMapper;
import com.casino.packs.repository.PackOpenHistoryRepository;
import com.casino.packs.repository.PackRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PackService {

    private final PackRepository packRepository;
    private final PackOpenHistoryRepository packOpenHistoryRepository;
    private final PackMapper packMapper;

    @Transactional(readOnly = true)
    public List<PackResponse> listPacks() {
        return packRepository.findAll().stream().map(packMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PackOpenHistoryItemResponse> history(long userId) {
        return packOpenHistoryRepository.findByUserIdOrderByIdDesc(userId).stream()
                .map(packMapper::toHistoryItem)
                .toList();
    }
}
