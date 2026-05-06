package com.sports.backend.match.v1.infrastructure.adapter.persistence.repository;

import com.sports.backend.match.v1.application.domain.model.Match;
import com.sports.backend.match.v1.application.domain.model.MatchStatistics;
import com.sports.backend.match.v1.application.domain.port.MatchPort;
import com.sports.backend.match.v1.infrastructure.adapter.persistence.model.converter.MatchEntityConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class MatchRepository implements MatchPort {

    private final MatchJpaRepository jpaRepository;
    private final MatchStatisticsJpaRepository statisticsJpaRepository;
    private final MatchEntityConverter converter;

    @Override
    public Optional<Match> findById(final Long id) {
        return jpaRepository.findById(id).map(converter::toDomain);
    }

    @Override
    public Optional<Match> findByApiId(final Long apiId) {
        return jpaRepository.findByApiId(apiId).map(converter::toDomain);
    }

    @Override
    public List<Match> findByTeamId(final Long teamId, final int limit) {
        return converter.toDomainList(jpaRepository.findFinishedByTeamId(teamId, limit));
    }

    @Override
    public List<Match> findByTeamId(final Long teamId, final int limit, final String season) {
        return converter.toDomainList(jpaRepository.findFinishedByTeamIdAndSeason(teamId, limit, season));
    }

    @Override
    public List<Match> findAllByTeamId(final Long teamId) {
        return converter.toDomainList(jpaRepository.findAllFinishedByTeamId(teamId));
    }

    @Override
    public List<Match> findAllByTeamId(final Long teamId, final String season) {
        return converter.toDomainList(jpaRepository.findAllFinishedByTeamIdAndSeason(teamId, season));
    }

    @Override
    public List<String> findDistinctSeasonsByTeamId(final Long teamId) {
        return jpaRepository.findDistinctSeasonsByTeamId(teamId);
    }

    @Override
    public List<Match> findByBothTeamIds(final Long team1Id, final Long team2Id) {
        return converter.toDomainList(jpaRepository.findByBothTeams(team1Id, team2Id));
    }

    @Override
    public List<Match> findByBothTeamIds(final Long team1Id, final Long team2Id, final String season) {
        return converter.toDomainList(jpaRepository.findByBothTeamsAndSeason(team1Id, team2Id, season));
    }

    @Override
    public List<Match> findByCompetitionIdAndSeason(final Long competitionId, final String season) {
        return converter.toDomainList(jpaRepository.findByCompetitionIdAndSeason(competitionId, season));
    }

    @Override
    public List<Long> findDistinctTeamIdsByCompetitionAndSeason(final Long competitionId, final String season) {
        final Set<Long> ids = new HashSet<>();
        ids.addAll(jpaRepository.findDistinctHomeTeamIdsByCompetitionAndSeason(competitionId, season));
        ids.addAll(jpaRepository.findDistinctAwayTeamIdsByCompetitionAndSeason(competitionId, season));
        return new ArrayList<>(ids);
    }

    @Override
    public Match save(final Match match) {
        return converter.toDomain(jpaRepository.save(converter.toEntity(match)));
    }

    @Override
    public void saveStatistics(final MatchStatistics statistics) {
        statisticsJpaRepository.save(converter.toEntity(statistics));
    }
}
