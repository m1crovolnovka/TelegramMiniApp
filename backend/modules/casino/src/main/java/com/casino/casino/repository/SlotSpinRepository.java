package com.casino.casino.repository;

import com.casino.casino.entity.SlotSpin;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotSpinRepository extends JpaRepository<SlotSpin, Long> {

    List<SlotSpin> findByUserIdOrderByIdDesc(long userId);
}
