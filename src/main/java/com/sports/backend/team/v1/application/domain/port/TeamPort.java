package com.sports.backend.team.v1.application.domain.port;

import com.sports.backend.team.v1.application.domain.model.Team;

import java.util.List;
import java.util.Optional;

public interface TeamPort {

    List<Team> findAll();

    List<Team> findByCompetitionId(Long competitionId);

    List<Team> findAllByIds(List<Long> ids);

    Optional<Team> findById(Long id);

    Optional<Team> findByApiId(Integer apiId);

    Team save(Team team);
}
