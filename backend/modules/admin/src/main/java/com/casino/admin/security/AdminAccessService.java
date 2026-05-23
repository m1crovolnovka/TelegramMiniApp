package com.casino.admin.security;

import com.casino.admin.config.AdminAccessProperties;
import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;
import com.casino.users.entity.User;
import com.casino.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAccessService {

    private final AdminAccessProperties adminAccessProperties;
    private final UserRepository userRepository;

    public void requireAdmin(long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN, "User not found"));
        String username = user.getUsername();
        if (username == null || username.isBlank()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Admin: username required");
        }
        String normalized = username.trim().toLowerCase().replace("@", "");
        System.out.println(normalized);
        boolean allowed =
                adminAccessProperties.getAllowedUsernames().stream()
                        .map(s -> s.trim().toLowerCase().replace("@", ""))
                        .anyMatch(normalized::equals);
        System.out.println(allowed);
        if (!allowed) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Admin access denied");
        }
    }
}
