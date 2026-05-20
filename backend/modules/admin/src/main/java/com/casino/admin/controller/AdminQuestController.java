package com.casino.admin.controller;

import com.casino.quests.dto.request.CreateQuestRequest;
import com.casino.quests.dto.response.QuestResponse;
import com.casino.quests.service.QuestManagementService;
import com.casino.quests.service.QuestModerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/quests")
@RequiredArgsConstructor
public class AdminQuestController {

    private final QuestManagementService questManagementService;
    private final QuestModerationService questModerationService;

    @PostMapping
    public QuestResponse create(@Valid @RequestBody CreateQuestRequest body) {
        return questManagementService.createQuest(body);
    }

    @PostMapping("/submissions/{submissionId}/approve")
    public void approve(@PathVariable long submissionId) {
        questModerationService.approve(submissionId);
    }

    @PostMapping("/submissions/{submissionId}/reject")
    public void reject(@PathVariable long submissionId) {
        questModerationService.reject(submissionId);
    }
}
