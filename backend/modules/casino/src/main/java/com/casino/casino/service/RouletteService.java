package com.casino.casino.service;

import com.casino.casino.dto.request.RouletteBetRequest;
import com.casino.casino.dto.response.RouletteResultResponse;
import com.casino.casino.entity.RouletteBet;
import com.casino.casino.entity.RouletteBetType;
import com.casino.casino.exception.CasinoException;
import com.casino.casino.repository.RouletteBetRepository;
import com.casino.casino.rtp.RTPService;
import com.casino.economy.api.EconomyPort;
import com.casino.economy.entity.TransactionType;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RouletteService {

    private static final Set<Integer> REDS =
            Set.of(
                    1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36);

    private final EconomyPort economyPort;
    private final RouletteBetRepository rouletteBetRepository;
    private final RTPService rtpService;

    @Transactional
    public RouletteResultResponse bet(long userId, RouletteBetRequest req) {
        if (req.betType() == RouletteBetType.NUMBER
                && (req.numberValue() == null || req.numberValue() < 0 || req.numberValue() > 36)) {
            throw new CasinoException("numberValue required for NUMBER bet");
        }
        String debitId = "casino:roulette:debit:" + UUID.randomUUID();
        economyPort.debit(userId, req.stake(), debitId, TransactionType.CASINO_BET, "roulette_bet");
        int rolled = rtpService.rollRouletteValue();
        long payout = computePayout(req.betType(), req.numberValue(), rolled, req.stake());
        if (payout > 0) {
            String creditId = "casino:roulette:credit:" + UUID.randomUUID();
            economyPort.credit(userId, payout, creditId, TransactionType.CASINO_WIN, "roulette_win");
        }
        rouletteBetRepository.save(
                new RouletteBet(
                        userId, req.betType(), req.numberValue(), req.stake(), payout, rolled));
        return new RouletteResultResponse(rolled, payout);
    }

    private long computePayout(RouletteBetType type, Integer number, int rolled, long stake) {
        if (type == RouletteBetType.NUMBER) {
            return number != null && number == rolled ? stake * 36 : 0;
        }
        if (rolled == 0) {
            return 0;
        }
        return switch (type) {
            case RED -> REDS.contains(rolled) ? stake * 2 : 0;
            case BLACK -> !REDS.contains(rolled) ? stake * 2 : 0;
            case ODD -> rolled % 2 == 1 ? stake * 2 : 0;
            case EVEN -> rolled % 2 == 0 ? stake * 2 : 0;
            case NUMBER -> 0;
        };
    }

    @Transactional(readOnly = true)
    public List<RouletteBet> history(long userId) {
        return rouletteBetRepository.findByUserIdOrderByIdDesc(userId);
    }
}
