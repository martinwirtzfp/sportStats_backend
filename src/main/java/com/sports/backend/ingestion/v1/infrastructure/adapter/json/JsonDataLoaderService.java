package com.sports.backend.ingestion.v1.infrastructure.adapter.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sports.backend.competition.v1.application.domain.port.CompetitionPort;
import com.sports.backend.ingestion.v1.infrastructure.adapter.apiclient.model.ApiFixturesResponse;
import com.sports.backend.ingestion.v1.infrastructure.adapter.apiclient.model.ApiTeamsResponse;
import com.sports.backend.match.v1.application.domain.model.Match;
import com.sports.backend.match.v1.application.domain.port.MatchPort;
import com.sports.backend.team.v1.application.domain.model.Team;
import com.sports.backend.team.v1.application.domain.port.TeamPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class JsonDataLoaderService {

    private final ObjectMapper objectMapper;
    private final TeamPort teamPort;
    private final MatchPort matchPort;
    private final CompetitionPort competitionPort;

    public void loadTeams(final Resource resource, final Long competitionId) throws IOException {
        log.info("Loading teams from JSON: {}", resource.getFilename());
        final ApiTeamsResponse response = objectMapper.readValue(resource.getInputStream(), ApiTeamsResponse.class);

        if (response.getResponse() == null) return;

        for (final ApiTeamsResponse.TeamEntry entry : response.getResponse()) {
            final ApiTeamsResponse.TeamInfo info = entry.getTeam();
            if (teamPort.findByApiId(info.getId()).isPresent()) {
                log.debug("Team apiId#{} already exists, skipping", info.getId());
                continue;
            }
            final Team team = new Team();
            team.setName(info.getName());
            team.setShortName(info.getCode() != null
                    ? info.getCode()
                    : info.getName().substring(0, Math.min(3, info.getName().length())).toUpperCase());
            team.setLogoUrl(info.getLogo());
            team.setApiId(info.getId());
            team.setCompetitionId(competitionId);
            teamPort.save(team);
            log.debug("Saved team: {}", info.getName());
        }
        log.info("Teams loaded from JSON successfully");
    }

    public void loadFixtures(final Resource resource, final Long competitionId, final String season) throws IOException {
        log.info("Loading fixtures from JSON: {}", resource.getFilename());
        final ApiFixturesResponse response = objectMapper.readValue(resource.getInputStream(), ApiFixturesResponse.class);

        if (response.getResponse() == null) return;

        final String competitionName = competitionPort.findById(competitionId)
                .map(c -> c.getName())
                .orElse("");

        for (final ApiFixturesResponse.FixtureEntry entry : response.getResponse()) {
            final Long apiId = entry.getFixture().getId();
            if (matchPort.findByApiId(apiId).isPresent()) {
                log.debug("Match apiId#{} already exists, skipping", apiId);
                continue;
            }

            final ApiFixturesResponse.TeamsInfo teams = entry.getTeams();
            final Long homeTeamId = resolveTeamId(teams.getHome().getId(), teams.getHome().getName(), teams.getHome().getLogo(), competitionId);
            final Long awayTeamId = resolveTeamId(teams.getAway().getId(), teams.getAway().getName(), teams.getAway().getLogo(), competitionId);

            final Match match = new Match();
            match.setApiId(apiId);
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
        log.info("Fixtures loaded from JSON successfully");
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
