package com.casino.questbot.repo;

import com.casino.questbot.entity.QuestAssignmentEntity;
import com.casino.questbot.entity.TaskStatus;
import com.casino.questbot.entity.UserEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestAssignmentRepository extends JpaRepository<QuestAssignmentEntity, Long> {

    @Query(
            """
            select a from QuestAssignmentEntity a
            where (a.userA = :user or a.userB = :user) and a.status = :status
            order by a.createdAt desc
            """)
    List<QuestAssignmentEntity> findByUserAndStatusOrderByCreatedAtDesc(
            @Param("user") UserEntity user, @Param("status") TaskStatus status);

    default Optional<QuestAssignmentEntity> findActiveForUser(UserEntity user) {
        List<QuestAssignmentEntity> list = findByUserAndStatusOrderByCreatedAtDesc(user, TaskStatus.ASSIGNED);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Query(
            """
            select distinct a.task.id from QuestAssignmentEntity a
            where a.userA = :user or a.userB = :user
            """)
    Collection<Long> findTaskIdsEverUsedByUser(@Param("user") UserEntity user);

    long countByStatus(TaskStatus status);

    Optional<QuestAssignmentEntity> findFirstByStatusOrderByCreatedAtAsc(TaskStatus status);
}
