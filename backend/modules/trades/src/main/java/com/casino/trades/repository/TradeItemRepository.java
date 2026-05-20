package com.casino.trades.repository;

import com.casino.trades.entity.TradeItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeItemRepository extends JpaRepository<TradeItem, Long> {

    List<TradeItem> findByTradeId(long tradeId);
}
