package com.casino.questbot.repo;

import com.casino.questbot.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameIgnoreCase(String username);
}
