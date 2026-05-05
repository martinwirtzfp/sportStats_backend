package com.sports.backend.team.v1.application.service;

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

    public List<Team> findAll() {
        log.debug("Fetching all teams");
        return teamPort.findAll();
    }

    public List<Team> findByCompetitionId(final Long competitionId) {
        log.debug("Fetching teams for competition id#{}", competitionId);
        return teamPort.findByCompetitionId(competitionId);
    }

    public Team findById(final Long id) {
        log.debug("Fetching team id#{}", id);
        return teamPort.findById(id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TEAM_NOT_FOUND));
    }
}
