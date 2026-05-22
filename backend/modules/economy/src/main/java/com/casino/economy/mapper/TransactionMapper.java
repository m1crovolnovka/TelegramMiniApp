package com.casino.economy.mapper;

import com.casino.economy.dto.response.TransactionResponse;
import com.casino.economy.entity.LedgerEntry;
import com.casino.economy.entity.TransactionType;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(LedgerEntry e) {
        return new TransactionResponse(
                e.getId(),
                e.getAmount(),
                e.getOperationId(),
                e.getTransactionType(),
                e.getReason(),
                describe(e.getTransactionType(), e.getReason(), e.getAmount()),
                e.getCreatedAt());
    }

    private static String transferDescription(String reason, long amount) {
        if (reason != null && reason.startsWith("transfer:")) {
            String[] p = reason.split(":");
            if (p.length >= 3) {
                long from = Long.parseLong(p[1]);
                long to = Long.parseLong(p[2]);
                if (amount < 0) {
                    return "Отправлено пользователю #" + to;
                }
                return "Получено от пользователя #" + from;
            }
        }
        return amount >= 0 ? "Получено (перевод)" : "Отправлено (перевод)";
    }

    private static String describe(TransactionType type, String reason, long amount) {
        String sign = amount >= 0 ? "+" : "";
        return switch (type) {
            case PACK_PURCHASE -> "Покупка пака";
            case CASINO_BET -> "Ставка в казино";
            case CASINO_WIN -> "Выигрыш в казино";
            case QUEST_REWARD -> "Награда за квест";
            case BET_STAKE -> "Ставка на событие";
            case BET_PAYOUT -> "Выплата по ставке";
            case TRADE_FEE, TRANSFER -> transferDescription(reason, amount);
            case ADMIN_ADJUST -> amount >= 0 ? "Начисление админом" : "Списание админом";
            case SALARY -> "Почасовая зарплата";
            default -> reason != null ? reason : type.name();
        } + " (" + sign + amount + " 🪙)";
    }
}
