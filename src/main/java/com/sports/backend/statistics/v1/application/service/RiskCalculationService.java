package com.sports.backend.statistics.v1.application.service;

import com.sports.backend.match.v1.application.domain.model.Match;
import com.sports.backend.match.v1.application.domain.port.MatchPort;
import com.sports.backend.shared.v1.application.exception.ApplicationException;
import com.sports.backend.shared.v1.application.exception.error.ApplicationError;
import com.sports.backend.statistics.v1.application.domain.model.Probability1X2;
import com.sports.backend.statistics.v1.application.domain.model.RiskAnalysis;
import com.sports.backend.team.v1.application.domain.port.TeamPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Risk calculation service.
 *
 * <p>Computes betting probabilities based on recent match history using a
 * weighted recency model: more recent matches receive higher weight via
 * exponential decay (weight = e^(-lambda * index)).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RiskCalculationService {

    private static final double DECAY_LAMBDA = 0.1;
    private static final double OVER_UNDER_THRESHOLD = 2.5;

    private final MatchPort matchPort;
    private final TeamPort teamPort;

    public RiskAnalysis calculate(final Long homeTeamId, final Long awayTeamId, final int lastN, final String season) {
        log.debug("Calculating risk for home#{} vs away#{} over {} matches season={}", homeTeamId, awayTeamId, lastN, season);

        final var homeTeam = teamPort.findById(homeTeamId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TEAM_NOT_FOUND));
        final var awayTeam = teamPort.findById(awayTeamId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TEAM_NOT_FOUND));

        final List<Match> homeMatches;
        final List<Match> awayMatches;
        if (lastN <= 0) {
            homeMatches = season != null ? matchPort.findAllByTeamId(homeTeamId, season) : matchPort.findAllByTeamId(homeTeamId);
            awayMatches = season != null ? matchPort.findAllByTeamId(awayTeamId, season) : matchPort.findAllByTeamId(awayTeamId);
        } else {
            homeMatches = season != null ? matchPort.findByTeamId(homeTeamId, lastN, season) : matchPort.findByTeamId(homeTeamId, lastN);
            awayMatches = season != null ? matchPort.findByTeamId(awayTeamId, lastN, season) : matchPort.findByTeamId(awayTeamId, lastN);
        }

        final RiskAnalysis risk = new RiskAnalysis();
        risk.setHomeTeamId(homeTeamId);
        risk.setHomeTeamName(homeTeam.getName());
        risk.setAwayTeamId(awayTeamId);
        risk.setAwayTeamName(awayTeam.getName());
        risk.setLastN(lastN);

        risk.setProbability1X2(calc1X2(homeTeamId, awayTeamId, homeMatches, awayMatches));
        risk.setHalfTimeProbability(calcHalfTime(homeTeamId, awayTeamId, homeMatches, awayMatches));
        calcOverUnderAndBtts(risk, homeMatches, awayMatches);

        return risk;
    }

    /**
     * 1X2 probability using weighted win/draw/loss rates per team.
     *
     * <p>Home advantage: home team's home win rate + away team's away loss rate weighted 50/50.
     */
    private Probability1X2 calc1X2(
            final Long homeTeamId, final Long awayTeamId,
            final List<Match> homeMatches, final List<Match> awayMatches) {

        // Weighted rates for home team playing at home
        double homeWinWeight = 0, homeDrawWeight = 0, homeLossWeight = 0;
        double homeTotal = 0;
        for (int i = 0; i < homeMatches.size(); i++) {
            final Match m = homeMatches.get(i);
            if (m.getHomeGoals() == null || m.getAwayGoals() == null) continue;
            final double w = Math.exp(-DECAY_LAMBDA * i);
            homeTotal += w;
            final boolean isHome = m.getHomeTeamId().equals(homeTeamId);
            final int scored = isHome ? m.getHomeGoals() : m.getAwayGoals();
            final int conceded = isHome ? m.getAwayGoals() : m.getHomeGoals();
            if (scored > conceded) homeWinWeight += w;
            else if (scored == conceded) homeDrawWeight += w;
            else homeLossWeight += w;
        }

        // Weighted rates for away team playing away
        double awayWinWeight = 0, awayDrawWeight = 0, awayLossWeight = 0;
        double awayTotal = 0;
        for (int i = 0; i < awayMatches.size(); i++) {
            final Match m = awayMatches.get(i);
            if (m.getHomeGoals() == null || m.getAwayGoals() == null) continue;
            final double w = Math.exp(-DECAY_LAMBDA * i);
            awayTotal += w;
            final boolean isHome = m.getHomeTeamId().equals(awayTeamId);
            final int scored = isHome ? m.getHomeGoals() : m.getAwayGoals();
            final int conceded = isHome ? m.getAwayGoals() : m.getHomeGoals();
            if (scored > conceded) awayWinWeight += w;
            else if (scored == conceded) awayDrawWeight += w;
            else awayLossWeight += w;
        }

        // Normalize to [0,1]
        final double homeWinRate = homeTotal > 0 ? homeWinWeight / homeTotal : 0.33;
        final double homeDrawRate = homeTotal > 0 ? homeDrawWeight / homeTotal : 0.33;
        final double awayWinRate = awayTotal > 0 ? awayWinWeight / awayTotal : 0.33;
        final double awayDrawRate = awayTotal > 0 ? awayDrawWeight / awayTotal : 0.33;
        final double awayLossRate = awayTotal > 0 ? awayLossWeight / awayTotal : 0.33;

        // Blend: home win = home team's win rate contribution vs away team's loss rate
        double rawHome = (homeWinRate + awayLossRate) / 2.0;
        double rawDraw = (homeDrawRate + awayDrawRate) / 2.0;
        double rawAway = (awayWinRate + (homeTotal > 0 ? homeLossWeight / homeTotal : 0.33)) / 2.0;

        // Normalize to 100%
        final double sum = rawHome + rawDraw + rawAway;
        if (sum == 0) {
            rawHome = rawDraw = rawAway = 1.0 / 3;
        }

        final Probability1X2 p = new Probability1X2();
        p.setHomeWin(round(rawHome / sum * 100));
        p.setDraw(round(rawDraw / sum * 100));
        p.setAwayWin(round(rawAway / sum * 100));
        return p;
    }

    /**
     * Half-time 1X2 using ht_home_goals and ht_away_goals.
     */
    private Probability1X2 calcHalfTime(
            final Long homeTeamId, final Long awayTeamId,
            final List<Match> homeMatches, final List<Match> awayMatches) {

        int htHome = 0, htDraw = 0, htAway = 0, htTotal = 0;
        for (final Match m : homeMatches) {
            if (m.getHtHomeGoals() == null || m.getHtAwayGoals() == null) continue;
            htTotal++;
            final boolean isHome = m.getHomeTeamId().equals(homeTeamId);
            final int scored = isHome ? m.getHtHomeGoals() : m.getHtAwayGoals();
            final int conceded = isHome ? m.getHtAwayGoals() : m.getHtHomeGoals();
            if (scored > conceded) htHome++;
            else if (scored == conceded) htDraw++;
            else htAway++;
        }
        for (final Match m : awayMatches) {
            if (m.getHtHomeGoals() == null || m.getHtAwayGoals() == null) continue;
            htTotal++;
            final boolean isHome = m.getHomeTeamId().equals(awayTeamId);
            final int scored = isHome ? m.getHtHomeGoals() : m.getHtAwayGoals();
            final int conceded = isHome ? m.getHtAwayGoals() : m.getHtHomeGoals();
            if (scored > conceded) htAway++;
            else if (scored == conceded) htDraw++;
            else htHome++;
        }

        final Probability1X2 p = new Probability1X2();
        if (htTotal == 0) {
            p.setHomeWin(33.33);
            p.setDraw(33.33);
            p.setAwayWin(33.34);
        } else {
            p.setHomeWin(round((double) htHome / htTotal * 100));
            p.setDraw(round((double) htDraw / htTotal * 100));
            p.setAwayWin(round((double) htAway / htTotal * 100));
        }
        return p;
    }

    /**
     * Over/Under 2.5 and BTTS from the combined match pool.
     */
    private void calcOverUnderAndBtts(
            final RiskAnalysis risk,
            final List<Match> homeMatches,
            final List<Match> awayMatches) {

        int overCount = 0, bttsCount = 0, totalCount = 0;
        double totalGoals = 0;

        for (final Match m : homeMatches) {
            if (m.getHomeGoals() == null || m.getAwayGoals() == null) continue;
            totalCount++;
            final int goals = m.getHomeGoals() + m.getAwayGoals();
            totalGoals += goals;
            if (goals > OVER_UNDER_THRESHOLD) overCount++;
            if (m.getHomeGoals() > 0 && m.getAwayGoals() > 0) bttsCount++;
        }
        for (final Match m : awayMatches) {
            if (m.getHomeGoals() == null || m.getAwayGoals() == null) continue;
            totalCount++;
            final int goals = m.getHomeGoals() + m.getAwayGoals();
            totalGoals += goals;
            if (goals > OVER_UNDER_THRESHOLD) overCount++;
            if (m.getHomeGoals() > 0 && m.getAwayGoals() > 0) bttsCount++;
        }

        risk.setAvgTotalGoals(totalCount > 0 ? round(totalGoals / totalCount) : 0.0);
        risk.setOverPercentage(totalCount > 0 ? round((double) overCount / totalCount * 100) : 50.0);
        risk.setUnderPercentage(totalCount > 0 ? round(100.0 - (double) overCount / totalCount * 100) : 50.0);
        risk.setBttsYesPercentage(totalCount > 0 ? round((double) bttsCount / totalCount * 100) : 50.0);
        risk.setBttsNoPercentage(totalCount > 0 ? round(100.0 - (double) bttsCount / totalCount * 100) : 50.0);
    }

    private double round(final double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
