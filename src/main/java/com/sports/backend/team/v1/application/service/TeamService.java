package com.sports.backend.team.v1.application.service;

import com.sports.backend.match.v1.application.domain.port.MatchPort;
import com.sports.backend.shared.v1.application.exception.ApplicationException;
import com.sports.backend.shared.v1.application.exception.error.ApplicationError;
import com.sports.backend.team.v1.application.domain.model.Team;
import com.sports.backend.team.v1.application.domain.port.TeamPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {

    private final TeamPort teamPort;
    private final MatchPort matchPort;

    public List<Team> findAll() {
        log.debug("Fetching all teams");
        return teamPort.findAll();
    }

    public List<Team> findByCompetitionId(final Long competitionId) {
        log.debug("Fetching teams for competition id#{}", competitionId);
        return teamPort.findByCompetitionId(competitionId);
    }

    public List<Team> findByCompetitionAndSeason(final Long competitionId, final String season) {
        log.debug("Fetching teams for competition id#{} season={} from match history", competitionId, season);
        final List<Long> teamIds = matchPort.findDistinctTeamIdsByCompetitionAndSeason(competitionId, season);
        return teamPort.findAllByIds(teamIds);
    }

    public Team findById(final Long id) {
        log.debug("Fetching team id#{}", id);
        return teamPort.findById(id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TEAM_NOT_FOUND));
    }

    public List<String> findTeamSeasons(final Long teamId) {
        log.debug("Fetching available seasons for team id#{}", teamId);
        teamPort.findById(teamId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TEAM_NOT_FOUND));
        return matchPort.findDistinctSeasonsByTeamId(teamId);
    }
}
