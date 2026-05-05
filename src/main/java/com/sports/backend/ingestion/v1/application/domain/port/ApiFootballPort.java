package com.sports.backend.ingestion.v1.application.domain.port;

public interface ApiFootballPort {

    void ingestTeams(Integer leagueApiId, String season, Long competitionId);

    void ingestMatches(Integer leagueApiId, String season, Long competitionId);
}
