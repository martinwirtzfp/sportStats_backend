package com.sports.backend.ingestion.v1.application.domain.port;

public interface ApiFootballPort {

    int ingestTeams(Integer leagueApiId, String season, Long competitionId);

    int ingestMatches(Integer leagueApiId, String season, Long competitionId);
}
