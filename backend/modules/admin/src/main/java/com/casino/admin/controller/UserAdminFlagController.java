package com.casino.admin.controller;

import com.casino.admin.dto.response.AdminFlagResponse;
import com.casino.admin.security.AdminAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserAdminFlagController {

    private final AdminAccessService adminAccessService;

    @GetMapping("/me/is-admin")
    public AdminFlagResponse isAdmin(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Long userId)) {
            return new AdminFlagResponse(false);
        }
        return new AdminFlagResponse(adminAccessService.isAdmin(userId));
    }
}
