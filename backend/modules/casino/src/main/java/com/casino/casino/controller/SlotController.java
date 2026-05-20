package com.casino.casino.controller;

import com.casino.casino.dto.request.SpinRequest;
import com.casino.casino.entity.SlotSpin;
import com.casino.casino.service.SlotService;
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
@RequestMapping("/api/casino/slots")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    @PostMapping("/spin")
    public long spin(Authentication authentication, @Valid @RequestBody SpinRequest body) {
        long userId = (Long) authentication.getPrincipal();
        return slotService.spin(userId, body.bet());
    }

    @GetMapping("/history")
    public List<SlotSpin> history(Authentication authentication) {
        long userId = (Long) authentication.getPrincipal();
        return slotService.history(userId);
    }
}
