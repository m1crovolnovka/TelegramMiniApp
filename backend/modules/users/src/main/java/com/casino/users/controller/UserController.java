package com.casino.users.controller;

import com.casino.cards.dto.response.InventoryResponse;
import com.casino.cards.service.CardCatalogService;
import com.casino.economy.api.EconomyPort;
import com.casino.users.dto.response.LeaderboardEntryResponse;
import com.casino.users.dto.response.PublicUserResponse;
import com.casino.users.dto.response.UserResponse;
import com.casino.users.entity.User;
import com.casino.users.mapper.UserMapper;
import com.casino.users.service.LeaderboardService;
import com.casino.users.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EconomyPort economyPort;
    private final UserMapper userMapper;
    private final LeaderboardService leaderboardService;
    private final CardCatalogService cardCatalogService;

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        long userId = (Long) authentication.getPrincipal();
        User user = userService.requireById(userId);
        long balance = economyPort.getBalance(userId);
        return userMapper.toResponse(user, balance);
    }

    @GetMapping("/leaderboard")
    public List<LeaderboardEntryResponse> leaderboard(@RequestParam(defaultValue = "50") int limit) {
        return leaderboardService.topByBalance(limit);
    }

    @GetMapping("/by-username/{username}")
    public PublicUserResponse byUsername(@PathVariable String username) {
        User user = userService.requireByUsername(username);
        return new PublicUserResponse(user.getId(), user.getUsername());
    }

    @GetMapping("/{id}")
    public PublicUserResponse byId(@PathVariable long id) {
        User user = userService.requireById(id);
        return new PublicUserResponse(user.getId(), user.getUsername());
    }

    @GetMapping("/{id}/inventory")
    public InventoryResponse inventory(@PathVariable long id) {
        return cardCatalogService.inventory(id);
    }
}
