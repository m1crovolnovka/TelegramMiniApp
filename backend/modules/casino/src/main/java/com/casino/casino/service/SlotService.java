package com.casino.casino.service;

import com.casino.casino.dto.response.SlotSpinResponse;
import com.casino.casino.entity.SlotSpin;
import com.casino.casino.repository.SlotSpinRepository;
import com.casino.casino.rtp.RTPService;
import com.casino.economy.api.EconomyPort;
import com.casino.economy.entity.TransactionType;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SlotService {

    private final EconomyPort economyPort;
    private final SlotSpinRepository slotSpinRepository;
    private final RTPService rtpService;

    @Transactional
    public SlotSpinResponse spin(long userId, long bet, String variant) {
        String gameVariant = variant == null || variant.isBlank() ? "sweet-bonanza" : variant;
        String debitId = "casino:slots:debit:" + UUID.randomUUID();
        economyPort.debit(userId, bet, debitId, TransactionType.CASINO_BET, "slots_bet");
        double mult = rtpService.rollSlotMultiplier();
        long payout = mult <= 0 ? 0 : Math.round(bet * mult);
        if (payout > 0) {
            String creditId = "casino:slots:credit:" + UUID.randomUUID();
            economyPort.credit(userId, payout, creditId, TransactionType.CASINO_WIN, "slots_payout");
        }
        List<String> symbols = SlotSymbolPicker.pickSymbols(gameVariant, mult);
        slotSpinRepository.save(new SlotSpin(userId, gameVariant.toUpperCase(), bet, payout));
        return new SlotSpinResponse(payout, gameVariant, symbols);
    }

    @Transactional(readOnly = true)
    public List<SlotSpin> history(long userId) {
        return slotSpinRepository.findByUserIdOrderByIdDesc(userId);
    }
}
