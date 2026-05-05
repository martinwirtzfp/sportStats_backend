package com.sports.backend.user.v1.infrastructure.adapter.persistence.repository;

import com.sports.backend.user.v1.infrastructure.adapter.persistence.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
