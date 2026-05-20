package com.casino.admin.controller;

import com.casino.admin.dto.response.AdminStatsApiResponse;
import com.casino.admin.mapper.AdminMapper;
import com.casino.admin.service.AdminFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminFacadeService adminFacadeService;
    private final AdminMapper adminMapper;

    @GetMapping("/stats")
    public AdminStatsApiResponse stats() {
        return adminMapper.toApi(adminFacadeService.stats());
    }
}
