package com.sports.backend.statistics.v1.application.service;

import com.sports.backend.match.v1.application.domain.model.Match;
import com.sports.backend.match.v1.application.domain.port.MatchPort;
import com.sports.backend.shared.v1.application.exception.ApplicationException;
import com.sports.backend.shared.v1.application.exception.error.ApplicationError;
import com.sports.backend.statistics.v1.application.domain.model.TeamStats;
import com.sports.backend.team.v1.application.domain.port.TeamPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

    private final MatchPort matchPort;
    private final TeamPort teamPort;

    public TeamStats getTeamStats(final Long teamId, final int lastN, final String season) {
        log.debug("Computing stats for team id#{} over last {} matches season={}", teamId, lastN, season);

        final var team = teamPort.findById(teamId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TEAM_NOT_FOUND));

        final List<Match> matches;
        if (lastN <= 0) {
            matches = season != null
                    ? matchPort.findAllByTeamId(teamId, season)
                    : matchPort.findAllByTeamId(teamId);
        } else {
            matches = season != null
                    ? matchPort.findByTeamId(teamId, lastN, season)
                    : matchPort.findByTeamId(teamId, lastN);
        }

        final TeamStats stats = new TeamStats();
        stats.setTeamId(teamId);
        stats.setTeamName(team.getName());
        stats.setLastN(lastN);
        stats.setTotalMatches(matches.size());

        int wins = 0, draws = 0, losses = 0;
        int goalsScored = 0, goalsConceded = 0, cleanSheets = 0;
        int homeWins = 0, homeDraws = 0, homeLosses = 0;
        int awayWins = 0, awayDraws = 0, awayLosses = 0;

        for (final Match match : matches) {
            if (match.getHomeGoals() == null || match.getAwayGoals() == null) {
                continue;
            }
            final boolean isHome = match.getHomeTeamId().equals(teamId);
            final int scored = isHome ? match.getHomeGoals() : match.getAwayGoals();
            final int conceded = isHome ? match.getAwayGoals() : match.getHomeGoals();

            goalsScored += scored;
            goalsConceded += conceded;
            if (conceded == 0) {
                cleanSheets++;
            }

            if (scored > conceded) {
                wins++;
                if (isHome) homeWins++; else awayWins++;
            } else if (scored == conceded) {
                draws++;
                if (isHome) homeDraws++; else awayDraws++;
            } else {
                losses++;
                if (isHome) homeLosses++; else awayLosses++;
            }
        }

        final int total = matches.size();
        stats.setWins(wins);
        stats.setDraws(draws);
        stats.setLosses(losses);
        stats.setGoalsScored(goalsScored);
        stats.setGoalsConceded(goalsConceded);
        stats.setCleanSheets(cleanSheets);
        stats.setWinPercentage(total > 0 ? round((double) wins / total * 100) : 0.0);
        stats.setDrawPercentage(total > 0 ? round((double) draws / total * 100) : 0.0);
        stats.setLossPercentage(total > 0 ? round((double) losses / total * 100) : 0.0);
        stats.setGoalsScoredAvg(total > 0 ? round((double) goalsScored / total) : 0.0);
        stats.setGoalsConcededAvg(total > 0 ? round((double) goalsConceded / total) : 0.0);
        stats.setHomeWins(homeWins);
        stats.setHomeDraws(homeDraws);
        stats.setHomeLosses(homeLosses);
        stats.setAwayWins(awayWins);
        stats.setAwayDraws(awayDraws);
        stats.setAwayLosses(awayLosses);

        return stats;
    }

    private double round(final double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
