package com.sports.backend.ingestion.v1.application.service;

import com.sports.backend.competition.v1.application.domain.model.Competition;
import com.sports.backend.competition.v1.application.domain.port.CompetitionPort;
import com.sports.backend.ingestion.v1.application.domain.port.ApiFootballPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataIngestionService {

    private final ApiFootballPort apiFootballPort;
    private final CompetitionPort competitionPort;

    public void ingestLeague(final Integer leagueApiId, final String season, final String competitionName) {
        log.info("Starting ingestion for leagueApiId={} season={} competitionName={}", leagueApiId, season, competitionName);

        final Long competitionId = competitionPort.findByApiId(leagueApiId)
                .map(Competition::getId)
                .orElseGet(() -> {
                    final Competition competition = new Competition();
                    competition.setApiId(leagueApiId);
                    competition.setName(competitionName);
                    competition.setType("LEAGUE");
                    competition.setSeason(season);
                    final Competition saved = competitionPort.save(competition);
                    log.info("Created competition id={} name={}", saved.getId(), saved.getName());
                    return saved.getId();
                });

        apiFootballPort.ingestTeams(leagueApiId, season, competitionId);
        apiFootballPort.ingestMatches(leagueApiId, season, competitionId);
        log.info("Ingestion completed for leagueApiId={}", leagueApiId);
    }
}
