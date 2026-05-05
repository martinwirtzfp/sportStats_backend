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

    public String ingestLeague(final Integer leagueApiId, final String season, final String competitionName) {
        log.info("Starting ingestion for leagueApiId={} season={} competitionName={}", leagueApiId, season, competitionName);

        final boolean competitionAlreadyExisted = competitionPort.findByApiIdAndSeason(leagueApiId, season).isPresent();

        final Long competitionId = competitionAlreadyExisted
                ? competitionPort.findByApiIdAndSeason(leagueApiId, season).get().getId()
                : createCompetition(leagueApiId, season, competitionName);

        final int teamsImported = apiFootballPort.ingestTeams(leagueApiId, season, competitionId);
        final int matchesImported = apiFootballPort.ingestMatches(leagueApiId, season, competitionId);
        log.info("Ingestion completed for leagueApiId={}: {} teams, {} matches", leagueApiId, teamsImported, matchesImported);

        if (teamsImported == 0 && matchesImported == 0) {
            if (!competitionAlreadyExisted) {
                competitionPort.deleteById(competitionId);
                log.info("Deleted empty competition id={} (no data from API-Football)", competitionId);
            }
            return String.format("Advertencia: no se importaron datos para leagueApiId=%d season=%s. " +
                    "Comprueba que el ID de liga y la temporada sean correctos (ej: 2024).", leagueApiId, season);
        }
        return String.format("Importación completada: %d equipos y %d partidos para leagueApiId=%d season=%s.",
                teamsImported, matchesImported, leagueApiId, season);
    }

    private Long createCompetition(final Integer leagueApiId, final String season, final String competitionName) {
        final Competition competition = new Competition();
        competition.setApiId(leagueApiId);
        competition.setName(competitionName);
        competition.setType("LEAGUE");
        competition.setSeason(season);
        final Competition saved = competitionPort.save(competition);
        log.info("Created competition id={} name={} season={}", saved.getId(), saved.getName(), saved.getSeason());
        return saved.getId();
    }
}
