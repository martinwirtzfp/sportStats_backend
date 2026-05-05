package com.sports.backend.competition.v1.infrastructure.adapter.persistence.repository;

import com.sports.backend.competition.v1.infrastructure.adapter.persistence.model.CompetitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompetitionJpaRepository extends JpaRepository<CompetitionEntity, Long> {

    Optional<CompetitionEntity> findByApiId(Integer apiId);

    Optional<CompetitionEntity> findByApiIdAndSeason(Integer apiId, String season);
}
