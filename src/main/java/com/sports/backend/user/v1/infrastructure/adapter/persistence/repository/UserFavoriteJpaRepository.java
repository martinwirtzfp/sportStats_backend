package com.sports.backend.user.v1.infrastructure.adapter.persistence.repository;

import com.sports.backend.user.v1.infrastructure.adapter.persistence.model.UserFavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFavoriteJpaRepository extends JpaRepository<UserFavoriteEntity, Long> {

    List<UserFavoriteEntity> findByUserId(Long userId);

    Optional<UserFavoriteEntity> findByUserIdAndTeamId(Long userId, Long teamId);
}
