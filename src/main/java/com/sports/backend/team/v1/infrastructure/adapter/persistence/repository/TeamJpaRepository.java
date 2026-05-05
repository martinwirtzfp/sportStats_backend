package com.sports.backend.team.v1.infrastructure.adapter.persistence.repository;

import com.sports.backend.team.v1.infrastructure.adapter.persistence.model.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamJpaRepository extends JpaRepository<TeamEntity, Long> {

    List<TeamEntity> findByCompetitionId(Long competitionId);

    Optional<TeamEntity> findByApiId(Integer apiId);
}
