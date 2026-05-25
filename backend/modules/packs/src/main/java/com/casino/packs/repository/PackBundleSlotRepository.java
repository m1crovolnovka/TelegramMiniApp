package com.casino.packs.repository;

import com.casino.packs.entity.PackBundleSlot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackBundleSlotRepository extends JpaRepository<PackBundleSlot, Long> {

    List<PackBundleSlot> findByPackIdOrderByIdAsc(long packId);

    void deleteByPackId(long packId);
}
