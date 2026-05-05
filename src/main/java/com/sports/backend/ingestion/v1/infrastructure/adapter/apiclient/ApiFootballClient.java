package com.sports.backend.ingestion.v1.infrastructure.adapter.apiclient;

import com.sports.backend.competition.v1.application.domain.port.CompetitionPort;
import com.sports.backend.ingestion.v1.application.domain.port.ApiFootballPort;
import com.sports.backend.ingestion.v1.infrastructure.adapter.apiclient.model.ApiFixturesResponse;
import com.sports.backend.ingestion.v1.infrastructure.adapter.apiclient.model.ApiTeamsResponse;
import com.sports.backend.match.v1.application.domain.model.Match;
import com.sports.backend.match.v1.application.domain.port.MatchPort;
import com.sports.backend.shared.v1.application.exception.ApplicationException;
import com.sports.backend.shared.v1.application.exception.error.ApplicationError;
import com.sports.backend.team.v1.application.domain.model.Team;
import com.sports.backend.team.v1.application.domain.port.TeamPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.List;

@Component
@Slf4j
public class ApiFootballClient implements ApiFootballPort {

    private static final String FINISHED_STATUS = "FT";

    private final RestClient restClient;
    private final TeamPort teamPort;
    private final MatchPort matchPort;
    private final CompetitionPort competitionPort;

    public ApiFootballClient(
            @Value("${app.apifootball.base-url}") final String baseUrl,
            @Value("${app.apifootball.key}") final String apiKey,
            final TeamPort teamPort,
            final MatchPort matchPort,
            final CompetitionPort competitionPort) {
        this.teamPort = teamPort;
        this.matchPort = matchPort;
        this.competitionPort = competitionPort;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-rapidapi-key", apiKey)
                .defaultHeader("x-rapidapi-host", "v3.football.api-sports.io")
                .build();
    }

    @Override
    public void ingestTeams(final Integer leagueApiId, final String season, final Long competitionId) {
        log.info("Ingesting teams for league#{} season={}", leagueApiId, season);
        try {
            final ApiTeamsResponse response = restClient.get()
                    .uri("/teams?league={league}&season={season}", leagueApiId, season)
                    .retrieve()
                    .body(ApiTeamsResponse.class);

            if (response == null || response.getResponse() == null) {
                log.warn("No teams returned from API for league#{}", leagueApiId);
                return;
            }

            for (final ApiTeamsResponse.TeamEntry entry : response.getResponse()) {
                final ApiTeamsResponse.TeamInfo info = entry.getTeam();
                if (teamPort.findByApiId(info.getId()).isPresent()) {
                    log.debug("Team apiId#{} already exists, skipping", info.getId());
                    continue;
                }
                final Team team = new Team();
                team.setName(info.getName());
                team.setShortName(info.getCode() != null ? info.getCode() : info.getName().substring(0, Math.min(3, info.getName().length())).toUpperCase());
                team.setLogoUrl(info.getLogo());
                team.setApiId(info.getId());
                team.setCompetitionId(competitionId);
                teamPort.save(team);
                log.debug("Saved team: {}", info.getName());
            }
        } catch (final RestClientException ex) {
            log.error("Error calling API-Football /teams: {}", ex.getMessage());
            throw new ApplicationException(ApplicationError.APIFOOTBALL_ERROR, ex.getMessage());
        }
    }

    @Override
    public void ingestMatches(final Integer leagueApiId, final String season, final Long competitionId) {
        log.info("Ingesting matches for league#{} season={}", leagueApiId, season);
        try {
            final ApiFixturesResponse response = restClient.get()
                    .uri("/fixtures?league={league}&season={season}&status=FT", leagueApiId, season)
                    .retrieve()
                    .body(ApiFixturesResponse.class);

            if (response == null || response.getResponse() == null) {
                log.warn("No fixtures returned from API for league#{}", leagueApiId);
                return;
            }

            final String competitionName = competitionPort.findById(competitionId)
                    .map(c -> c.getName())
                    .orElse("");

            for (final ApiFixturesResponse.FixtureEntry entry : response.getResponse()) {
                final Long apiFixtureId = entry.getFixture().getId();
                if (matchPort.findByApiId(apiFixtureId).isPresent()) {
                    log.debug("Match apiId#{} already exists, skipping", apiFixtureId);
                    continue;
                }

                final ApiFixturesResponse.TeamsInfo teams = entry.getTeams();
                final Long homeTeamId = resolveTeamId(teams.getHome().getId(), teams.getHome().getName(), teams.getHome().getLogo(), competitionId);
                final Long awayTeamId = resolveTeamId(teams.getAway().getId(), teams.getAway().getName(), teams.getAway().getLogo(), competitionId);

                final Match match = new Match();
                match.setApiId(apiFixtureId);
                match.setHomeTeamId(homeTeamId);
                match.setAwayTeamId(awayTeamId);
                match.setHomeTeamName(teams.getHome().getName());
                match.setAwayTeamName(teams.getAway().getName());
                match.setHomeTeamLogo(teams.getHome().getLogo());
                match.setAwayTeamLogo(teams.getAway().getLogo());
                match.setStatus("FINISHED");
                match.setCompetitionId(competitionId);
                match.setCompetitionName(competitionName);
                match.setSeason(season);

                if (entry.getFixture().getDate() != null) {
                    match.setMatchDate(Instant.parse(entry.getFixture().getDate()));
                }
                if (entry.getGoals() != null) {
                    match.setHomeGoals(entry.getGoals().getHome());
                    match.setAwayGoals(entry.getGoals().getAway());
                }
                if (entry.getScore() != null && entry.getScore().getHalftime() != null) {
                    match.setHtHomeGoals(entry.getScore().getHalftime().getHome());
                    match.setHtAwayGoals(entry.getScore().getHalftime().getAway());
                }

                matchPort.save(match);
                log.debug("Saved match: {} vs {}", teams.getHome().getName(), teams.getAway().getName());
            }
        } catch (final RestClientException ex) {
            log.error("Error calling API-Football /fixtures: {}", ex.getMessage());
            throw new ApplicationException(ApplicationError.APIFOOTBALL_ERROR, ex.getMessage());
        }
    }

    private Long resolveTeamId(final Integer apiTeamId, final String name, final String logo, final Long competitionId) {
        return teamPort.findByApiId(apiTeamId)
                .map(Team::getId)
                .orElseGet(() -> {
                    final Team t = new Team();
                    t.setApiId(apiTeamId);
                    t.setName(name);
                    t.setShortName(name.substring(0, Math.min(3, name.length())).toUpperCase());
                    t.setLogoUrl(logo);
                    t.setCompetitionId(competitionId);
                    return teamPort.save(t).getId();
                });
    }
}
