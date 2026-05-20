package com.casino.users.controller;

import com.casino.economy.api.EconomyPort;
import com.casino.users.dto.response.UserResponse;
import com.casino.users.entity.User;
import com.casino.users.mapper.UserMapper;
import com.casino.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EconomyPort economyPort;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        long userId = (Long) authentication.getPrincipal();
        User user = userService.requireById(userId);
        long balance = economyPort.getBalance(userId);
        return userMapper.toResponse(user, balance);
    }
}
