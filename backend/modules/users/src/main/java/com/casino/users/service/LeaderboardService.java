package com.casino.users.service;

import com.casino.cards.service.CollectionService;
import com.casino.economy.entity.Wallet;
import com.casino.economy.repository.WalletRepository;
import com.casino.users.dto.response.LeaderboardEntryResponse;
import com.casino.users.entity.User;
import com.casino.users.repository.UserRepository;
import com.casino.users.util.StubUsernames;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final CollectionService collectionService;

    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> topByBalance(int limit) {
        int capped = Math.min(Math.max(limit, 1), 100);
        List<Wallet> wallets = walletRepository.findAllByOrderByBalanceDesc(PageRequest.of(0, capped));
        List<Long> userIds = wallets.stream().map(Wallet::getUserId).toList();
        Map<Long, User> users =
                userRepository.findAllById(userIds).stream()
                        .collect(Collectors.toMap(User::getId, Function.identity()));
        List<LeaderboardEntryResponse> result = new ArrayList<>();
        int rank = 1;
        for (Wallet w : wallets) {
            User u = users.get(w.getUserId());
            if (u != null && StubUsernames.isStub(u.getUsername())) {
                continue;
            }
            String name = u != null && u.getUsername() != null ? u.getUsername() : "User #" + w.getUserId();
            long students = collectionService.countUniqueStudentsOwned(w.getUserId());
            result.add(new LeaderboardEntryResponse(rank++, w.getUserId(), name, w.getBalance(), students));
        }
        return result;
    }
}
