package com.casino.quests.controller;

import com.casino.quests.dto.request.SubmitQuestRequest;
import com.casino.quests.dto.response.QuestResponse;
import com.casino.quests.dto.response.QuestSubmissionResponse;
import com.casino.quests.service.QuestService;
import com.casino.quests.service.QuestSubmissionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;
    private final QuestSubmissionService questSubmissionService;

    @GetMapping
    public List<QuestResponse> list() {
        return questService.listActive();
    }

    @PostMapping("/submit")
    public QuestSubmissionResponse submit(Authentication authentication, @Valid @RequestBody SubmitQuestRequest body) {
        long userId = (Long) authentication.getPrincipal();
        return questSubmissionService.submit(userId, body);
    }

    @GetMapping("/my-submissions")
    public List<QuestSubmissionResponse> mySubmissions(Authentication authentication) {
        long userId = (Long) authentication.getPrincipal();
        return questSubmissionService.mySubmissions(userId);
    }
}
