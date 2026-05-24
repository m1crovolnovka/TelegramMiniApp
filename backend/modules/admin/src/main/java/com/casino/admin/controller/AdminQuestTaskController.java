package com.casino.admin.controller;

import com.casino.quests.bot.entity.QuestTaskEntity;
import com.casino.quests.bot.service.AdminService;
import com.casino.quests.dto.request.CreateQuestTaskRequest;
import com.casino.quests.dto.response.QuestTaskResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/quest-tasks")
@RequiredArgsConstructor
public class AdminQuestTaskController {

    private final AdminService questTaskAdminService;

    @GetMapping
    public List<QuestTaskResponse> list() {
        return questTaskAdminService.listAll().stream().map(AdminQuestTaskController::toResponse).toList();
    }

    @PostMapping
    public QuestTaskResponse create(@Valid @RequestBody CreateQuestTaskRequest body) {
        QuestTaskEntity saved =
                questTaskAdminService.addNewTask(body.description(), body.rewardCoins());
        return toResponse(saved);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id) {
        questTaskAdminService.deleteTask(id);
    }

    private static QuestTaskResponse toResponse(QuestTaskEntity t) {
        return new QuestTaskResponse(t.getId(), t.getDescription(), t.getRewardCoins());
    }
}
