package com.sports.backend.match.v1.application.domain.port;

import com.sports.backend.match.v1.application.domain.model.Match;
import com.sports.backend.match.v1.application.domain.model.MatchStatistics;

import java.util.List;
import java.util.Optional;

public interface MatchPort {

    Optional<Match> findById(Long id);

    Optional<Match> findByApiId(Long apiId);

    List<Match> findByTeamId(Long teamId, int limit);

    List<Match> findByTeamId(Long teamId, int limit, String season);

    List<Match> findByBothTeamIds(Long team1Id, Long team2Id);

    List<Match> findByBothTeamIds(Long team1Id, Long team2Id, String season);

    List<Match> findByCompetitionIdAndSeason(Long competitionId, String season);

    List<Long> findDistinctTeamIdsByCompetitionAndSeason(Long competitionId, String season);

    Match save(Match match);

    void saveStatistics(MatchStatistics statistics);
}
