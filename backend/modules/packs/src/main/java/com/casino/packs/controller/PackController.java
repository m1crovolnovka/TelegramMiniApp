package com.casino.packs.controller;

import com.casino.packs.dto.request.OpenPackRequest;
import com.casino.packs.dto.response.OpenPackResponse;
import com.casino.packs.dto.response.PackOpenHistoryItemResponse;
import com.casino.packs.dto.response.PackResponse;
import com.casino.packs.service.PackOpeningService;
import com.casino.packs.service.PackService;
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
@RequestMapping("/api/packs")
@RequiredArgsConstructor
public class PackController {

    private final PackService packService;
    private final PackOpeningService packOpeningService;

    @GetMapping
    public List<PackResponse> list() {
        return packService.listPacks();
    }

    @PostMapping("/open")
    public OpenPackResponse open(Authentication authentication, @Valid @RequestBody OpenPackRequest body) {
        long userId = (Long) authentication.getPrincipal();
        return packOpeningService.openPack(userId, body.packId(), body.idempotencyKey());
    }

    @GetMapping("/history")
    public List<PackOpenHistoryItemResponse> history(Authentication authentication) {
        long userId = (Long) authentication.getPrincipal();
        return packService.history(userId);
    }
}
