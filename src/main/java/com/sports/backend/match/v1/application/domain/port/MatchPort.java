package com.sports.backend.match.v1.application.domain.port;

import com.sports.backend.match.v1.application.domain.model.Match;

import java.util.List;
import java.util.Optional;

public interface MatchPort {

    Optional<Match> findById(Long id);

    Optional<Match> findByApiId(Long apiId);

    List<Match> findByTeamId(Long teamId, int limit);

    List<Match> findByTeamId(Long teamId, int limit, String season);

    List<Match> findAllByTeamId(Long teamId);

    List<Match> findAllByTeamId(Long teamId, String season);

    List<String> findDistinctSeasonsByTeamId(Long teamId);

    List<Match> findByBothTeamIds(Long team1Id, Long team2Id);

    List<Match> findByBothTeamIds(Long team1Id, Long team2Id, String season);

    List<Long> findDistinctTeamIdsByCompetitionAndSeason(Long competitionId, String season);

    Match save(Match match);
}
