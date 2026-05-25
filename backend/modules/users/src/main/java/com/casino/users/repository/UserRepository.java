package com.casino.users.repository;

import com.casino.users.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByTelegramId(long telegramId);

    Optional<User> findByUsernameIgnoreCase(String username);

    List<User> findByIdNotAndUsernameIsNotNullOrderByUsernameAsc(long id, Pageable pageable);

    List<User> findByUsernameIgnoreCaseIn(List<String> usernames);
}
