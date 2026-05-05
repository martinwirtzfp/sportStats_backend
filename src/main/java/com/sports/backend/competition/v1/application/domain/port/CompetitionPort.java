package com.sports.backend.competition.v1.application.domain.port;

import com.sports.backend.competition.v1.application.domain.model.Competition;

import java.util.List;
import java.util.Optional;

public interface CompetitionPort {

    List<Competition> findAll();

    Optional<Competition> findById(Long id);

    Optional<Competition> findByApiId(Integer apiId);

    Optional<Competition> findByApiIdAndSeason(Integer apiId, String season);

    Competition save(Competition competition);

    void deleteById(Long id);
}
