package com.casino.users.mapper;

import com.casino.users.dto.response.UserResponse;
import com.casino.users.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user, long balanceCoins) {
        return new UserResponse(
                user.getId(), user.getTelegramId(), user.getUsername(), user.getRole(), balanceCoins);
    }
}
