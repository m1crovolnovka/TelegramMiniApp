package com.casino.packs.repository;

import com.casino.packs.entity.PackOpenHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackOpenHistoryRepository extends JpaRepository<PackOpenHistory, Long> {

    boolean existsByUserIdAndIdempotencyKey(long userId, String idempotencyKey);

    Optional<PackOpenHistory> findByUserIdAndIdempotencyKey(long userId, String idempotencyKey);

    List<PackOpenHistory> findByUserIdOrderByIdDesc(long userId);

    List<PackOpenHistory> findByUserIdAndIdempotencyKeyStartingWith(long userId, String prefix);
}
