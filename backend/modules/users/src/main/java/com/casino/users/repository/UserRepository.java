package com.casino.users.repository;

import com.casino.users.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByTelegramId(long telegramId);
}
