package com.sports.backend.ingestion.v1.application.service;

import com.sports.backend.ingestion.v1.application.domain.port.ApiFootballPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataIngestionService {

    private final ApiFootballPort apiFootballPort;

    public void ingestLeague(final Integer leagueApiId, final String season, final Long competitionId) {
        log.info("Starting ingestion for leagueApiId={} season={} competitionId={}", leagueApiId, season, competitionId);
        apiFootballPort.ingestTeams(leagueApiId, season, competitionId);
        apiFootballPort.ingestMatches(leagueApiId, season, competitionId);
        log.info("Ingestion completed for leagueApiId={}", leagueApiId);
    }
}
