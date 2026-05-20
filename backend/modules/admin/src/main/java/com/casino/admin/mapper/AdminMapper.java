package com.casino.admin.mapper;

import com.casino.admin.dto.response.AdminStatsApiResponse;
import com.casino.admin.service.AdminFacadeService;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {

    public AdminStatsApiResponse toApi(AdminFacadeService.AdminStatsResponse s) {
        return new AdminStatsApiResponse(s.users(), s.cardDefinitions(), s.quests());
    }
}
