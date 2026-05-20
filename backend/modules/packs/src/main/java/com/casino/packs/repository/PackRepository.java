package com.casino.packs.repository;

import com.casino.packs.entity.Pack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackRepository extends JpaRepository<Pack, Long> {}
