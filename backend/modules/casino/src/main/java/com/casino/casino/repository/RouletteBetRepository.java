package com.casino.casino.repository;

import com.casino.casino.entity.RouletteBet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouletteBetRepository extends JpaRepository<RouletteBet, Long> {

    List<RouletteBet> findByUserIdOrderByIdDesc(long userId);
}
