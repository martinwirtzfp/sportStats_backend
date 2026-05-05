package com.sports.backend.statistics.v1.application.service;

import com.sports.backend.match.v1.application.domain.model.Match;
import com.sports.backend.match.v1.application.domain.port.MatchPort;
import com.sports.backend.shared.v1.application.exception.ApplicationException;
import com.sports.backend.shared.v1.application.exception.error.ApplicationError;
import com.sports.backend.statistics.v1.application.domain.model.HeadToHead;
import com.sports.backend.team.v1.application.domain.port.TeamPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeadToHeadService {

    private final MatchPort matchPort;
    private final TeamPort teamPort;

    public HeadToHead getHeadToHead(final Long team1Id, final Long team2Id) {
        log.debug("Computing H2H between team#{} and team#{}", team1Id, team2Id);

        final var team1 = teamPort.findById(team1Id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TEAM_NOT_FOUND));
        final var team2 = teamPort.findById(team2Id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TEAM_NOT_FOUND));

        final List<Match> matches = matchPort.findByBothTeamIds(team1Id, team2Id);

        final HeadToHead h2h = new HeadToHead();
        h2h.setTeam1Id(team1Id);
        h2h.setTeam1Name(team1.getName());
        h2h.setTeam2Id(team2Id);
        h2h.setTeam2Name(team2.getName());
        h2h.setTotalMatches(matches.size());
        h2h.setRecentMatches(matches);

        int team1Wins = 0, draws = 0, team2Wins = 0;
        int team1Goals = 0, team2Goals = 0, totalGoals = 0, bttsCount = 0;

        for (final Match match : matches) {
            if (match.getHomeGoals() == null || match.getAwayGoals() == null) {
                continue;
            }
            final boolean team1IsHome = match.getHomeTeamId().equals(team1Id);
            final int t1g = team1IsHome ? match.getHomeGoals() : match.getAwayGoals();
            final int t2g = team1IsHome ? match.getAwayGoals() : match.getHomeGoals();

            team1Goals += t1g;
            team2Goals += t2g;
            totalGoals += t1g + t2g;

            if (t1g > t2g) team1Wins++;
            else if (t1g == t2g) draws++;
            else team2Wins++;

            if (t1g > 0 && t2g > 0) bttsCount++;
        }

        final int total = matches.size();
        h2h.setTeam1Wins(team1Wins);
        h2h.setDraws(draws);
        h2h.setTeam2Wins(team2Wins);
        h2h.setTeam1GoalsAvg(total > 0 ? round((double) team1Goals / total) : 0.0);
        h2h.setTeam2GoalsAvg(total > 0 ? round((double) team2Goals / total) : 0.0);
        h2h.setAvgTotalGoals(total > 0 ? round((double) totalGoals / total) : 0.0);
        h2h.setBttsCount(bttsCount);
        h2h.setBttsPercentage(total > 0 ? round((double) bttsCount / total * 100) : 0.0);

        return h2h;
    }

    private double round(final double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
