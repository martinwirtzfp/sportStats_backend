package com.sports.backend.match.v1.infrastructure.adapter.persistence.repository;

import com.sports.backend.match.v1.infrastructure.adapter.persistence.model.MatchStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchStatisticsJpaRepository extends JpaRepository<MatchStatisticsEntity, Long> {
}
