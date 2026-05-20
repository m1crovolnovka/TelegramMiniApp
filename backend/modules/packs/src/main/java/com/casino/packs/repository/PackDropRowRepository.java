package com.casino.packs.repository;

import com.casino.packs.entity.PackDropRow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackDropRowRepository extends JpaRepository<PackDropRow, Long> {

    List<PackDropRow> findByPackId(long packId);
}
