package com.sports.backend.match.v1.application.service;

import com.sports.backend.match.v1.application.domain.model.Match;
import com.sports.backend.match.v1.application.domain.port.MatchPort;
import com.sports.backend.shared.v1.application.exception.ApplicationException;
import com.sports.backend.shared.v1.application.exception.error.ApplicationError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchService {

    private final MatchPort matchPort;

    public Match findById(final Long id) {
        log.debug("Fetching match id#{}", id);
        return matchPort.findById(id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.MATCH_NOT_FOUND));
    }

    public List<Match> findLastMatchesByTeam(final Long teamId, final int lastN) {
        log.debug("Fetching last {} matches for team id#{}", lastN, teamId);
        return matchPort.findByTeamId(teamId, lastN);
    }

    public List<Match> findHeadToHead(final Long team1Id, final Long team2Id) {
        log.debug("Fetching H2H matches between team#{} and team#{}", team1Id, team2Id);
        return matchPort.findByBothTeamIds(team1Id, team2Id);
    }
}
