package com.casino.admin.controller;

import com.casino.admin.dto.request.CreateCardRequest;
import com.casino.admin.service.AdminCardService;
import com.casino.cards.dto.response.CardDefinitionResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
public class AdminCardController {

    private final AdminCardService adminCardService;

    @GetMapping
    public List<CardDefinitionResponse> list() {
        return adminCardService.list();
    }

    @PostMapping
    public CardDefinitionResponse create(@Valid @RequestBody CreateCardRequest body) {
        return adminCardService.create(body);
    }
}
