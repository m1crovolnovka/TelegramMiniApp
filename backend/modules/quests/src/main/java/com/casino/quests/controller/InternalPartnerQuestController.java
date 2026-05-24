package com.casino.quests.controller;

import com.casino.quests.dto.request.CompletePartnerQuestRequest;
import com.casino.quests.dto.response.PartnerQuestCompleteResponse;
import com.casino.quests.service.PartnerQuestRewardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/partner-quests")
@RequiredArgsConstructor
public class InternalPartnerQuestController {

    private final PartnerQuestRewardService partnerQuestRewardService;

    @PostMapping("/complete")
    public PartnerQuestCompleteResponse complete(@Valid @RequestBody CompletePartnerQuestRequest body) {
        return partnerQuestRewardService.complete(body);
    }
}
