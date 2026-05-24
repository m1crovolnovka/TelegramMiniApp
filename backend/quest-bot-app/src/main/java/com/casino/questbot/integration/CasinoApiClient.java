package com.casino.questbot.integration;

import com.casino.questbot.config.QuestBotProperties;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class CasinoApiClient {

    private final QuestBotProperties properties;

    public void ensureUser(String username, Long telegramId) {
        client()
                .post()
                .uri("/api/internal/users/ensure")
                .body(Map.of("username", normalize(username), "telegramId", telegramId))
                .retrieve()
                .toBodilessEntity();
    }

    public void completePartnerQuest(
            String externalAssignmentId, String partnerAUsername, String partnerBUsername, long rewardCoins) {
        client()
                .post()
                .uri("/api/internal/partner-quests/complete")
                .body(
                        Map.of(
                                "externalAssignmentId",
                                externalAssignmentId,
                                "partnerAUsername",
                                normalize(partnerAUsername),
                                "partnerBUsername",
                                normalize(partnerBUsername),
                                "rewardCoins",
                                rewardCoins))
                .retrieve()
                .toBodilessEntity();
    }

    private RestClient client() {
        return RestClient.builder()
                .baseUrl(properties.getCasino().getBaseUrl())
                .defaultHeader("X-Internal-Api-Key", properties.getCasino().getInternalApiKey())
                .build();
    }

    private static String normalize(String username) {
        return username.trim().toLowerCase().replace("@", "");
    }
}
