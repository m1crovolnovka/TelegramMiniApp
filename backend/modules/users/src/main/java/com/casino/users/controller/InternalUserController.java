package com.casino.users.controller;

import com.casino.users.dto.request.EnsureUserRequest;
import com.casino.users.dto.response.PublicUserResponse;
import com.casino.users.entity.User;
import com.casino.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @PostMapping("/ensure")
    public PublicUserResponse ensure(@Valid @RequestBody EnsureUserRequest body) {
        User user = userService.findOrCreateByUsername(body.username(), body.telegramId());
        return new PublicUserResponse(user.getId(), user.getUsername());
    }
}
