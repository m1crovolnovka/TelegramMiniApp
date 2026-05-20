package com.casino.cards.repository;

import com.casino.cards.entity.UserCard;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCardRepository extends JpaRepository<UserCard, Long> {

    List<UserCard> findByUserId(long userId);

    Optional<UserCard> findByUserIdAndCardDefinitionId(long userId, long cardDefinitionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select uc from UserCard uc where uc.userId = :userId and uc.cardDefinitionId = :cardDefinitionId")
    Optional<UserCard> lockByUserAndCard(
            @Param("userId") long userId, @Param("cardDefinitionId") long cardDefinitionId);
}
