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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Risk calculation service based on an independent Poisson model.
 *
 * <p>For each team, weighted attack and defense rates are computed from ALL
 * available historical matches (no season filter). Matches are weighted by
 * recency using exponential time-decay: {@code weight = e^(-KAPPA * days_ago)},
 * giving a half-life of ~231 days. Expected goals for each team are derived
 * by combining attack strength with opponent defensive weakness plus a home
 * advantage factor. A score-probability matrix is built from two independent
 * Poisson distributions and all betting markets (1X2, Over/Under, BTTS,
 * half-time) are derived from that matrix.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RiskCalculationService {

    /** Exponential decay rate per day (half-life ≈ 231 days ≈ 7.5 months). */
    private static final double DECAY_KAPPA = 0.003;

    /** Home teams score ~15% more goals on average across major leagues. */
    private static final double HOME_ADVANTAGE = 1.15;

    /** Maximum score simulated per team (captures >99.9% of real matches). */
    private static final int MAX_GOALS = 10;

    private final MatchPort matchPort;
    private final TeamPort teamPort;

    /**
     * Computes risk probabilities for a hypothetical match between {@code homeTeamId}
     * and {@code awayTeamId} using all available historical data for both teams.
     */
    public RiskAnalysis calculate(final Long homeTeamId, final Long awayTeamId) {
        log.debug("Calculating Poisson risk for home#{} vs away#{}", homeTeamId, awayTeamId);

        final var homeTeam = teamPort.findById(homeTeamId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TEAM_NOT_FOUND));
        final var awayTeam = teamPort.findById(awayTeamId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TEAM_NOT_FOUND));

        final List<Match> homeMatches = matchPort.findAllByTeamId(homeTeamId);
        final List<Match> awayMatches = matchPort.findAllByTeamId(awayTeamId);

        final RiskAnalysis risk = new RiskAnalysis();
        risk.setHomeTeamId(homeTeamId);
        risk.setHomeTeamName(homeTeam.getName());
        risk.setAwayTeamId(awayTeamId);
        risk.setAwayTeamName(awayTeam.getName());
        risk.setLastN(0);

        // --- Full-time expected goals via attack/defense blend ---
        final double[] homeRates = computeWeightedGoalRates(homeTeamId, homeMatches);
        final double[] awayRates = computeWeightedGoalRates(awayTeamId, awayMatches);
        // [0] = weighted avg goals scored, [1] = weighted avg goals conceded

        final double muHome = ((homeRates[0] + awayRates[1]) / 2.0) * HOME_ADVANTAGE;
        final double muAway = (awayRates[0] + homeRates[1]) / 2.0 / HOME_ADVANTAGE;

        final double[][] matrix = buildScoreMatrix(muHome, muAway);
        risk.setProbability1X2(derive1X2(matrix));
        calcGoalMarkets(risk, matrix, muHome, muAway);

        // --- Half-time expected goals ---
        final double[] homeHtRates = computeWeightedHtGoalRates(homeTeamId, homeMatches);
        final double[] awayHtRates = computeWeightedHtGoalRates(awayTeamId, awayMatches);

        final double muHomeHt = ((homeHtRates[0] + awayHtRates[1]) / 2.0) * HOME_ADVANTAGE;
        final double muAwayHt = (awayHtRates[0] + homeHtRates[1]) / 2.0 / HOME_ADVANTAGE;

        risk.setHalfTimeProbability(derive1X2(buildScoreMatrix(muHomeHt, muAwayHt)));

        return risk;
    }

    /**
     * Returns {@code [weightedAvgScored, weightedAvgConceded]} for the given team
     * across all provided matches, using date-based exponential decay weights.
     */
    private double[] computeWeightedGoalRates(final Long teamId, final List<Match> matches) {
        final Instant now = Instant.now();
        double wScored = 0, wConceded = 0, wTotal = 0;
        for (final Match m : matches) {
            if (m.getHomeGoals() == null || m.getAwayGoals() == null) continue;
            final long daysAgo = m.getMatchDate() != null
                    ? ChronoUnit.DAYS.between(m.getMatchDate(), now) : 365L;
            final double w = Math.exp(-DECAY_KAPPA * daysAgo);
            wTotal += w;
            final boolean isHome = teamId.equals(m.getHomeTeamId());
            wScored   += w * (isHome ? m.getHomeGoals() : m.getAwayGoals());
            wConceded += w * (isHome ? m.getAwayGoals() : m.getHomeGoals());
        }
        if (wTotal == 0) return new double[]{1.2, 1.2}; // fallback: typical league average
        return new double[]{wScored / wTotal, wConceded / wTotal};
    }

    /** Same as {@link #computeWeightedGoalRates} but using half-time goal columns. */
    private double[] computeWeightedHtGoalRates(final Long teamId, final List<Match> matches) {
        final Instant now = Instant.now();
        double wScored = 0, wConceded = 0, wTotal = 0;
        for (final Match m : matches) {
            if (m.getHtHomeGoals() == null || m.getHtAwayGoals() == null) continue;
            final long daysAgo = m.getMatchDate() != null
                    ? ChronoUnit.DAYS.between(m.getMatchDate(), now) : 365L;
            final double w = Math.exp(-DECAY_KAPPA * daysAgo);
            wTotal += w;
            final boolean isHome = teamId.equals(m.getHomeTeamId());
            wScored   += w * (isHome ? m.getHtHomeGoals() : m.getHtAwayGoals());
            wConceded += w * (isHome ? m.getHtAwayGoals() : m.getHtHomeGoals());
        }
        if (wTotal == 0) return new double[]{0.5, 0.5}; // fallback: ~HT league average
        return new double[]{wScored / wTotal, wConceded / wTotal};
    }

    /**
     * Builds an {@code (MAX_GOALS+1) × (MAX_GOALS+1)} score-probability matrix.
     * Entry {@code [i][j]} = P(homeGoals=i) × P(awayGoals=j) (independent Poisson).
     */
    private double[][] buildScoreMatrix(final double muHome, final double muAway) {
        final double[][] matrix = new double[MAX_GOALS + 1][MAX_GOALS + 1];
        for (int i = 0; i <= MAX_GOALS; i++) {
            for (int j = 0; j <= MAX_GOALS; j++) {
                matrix[i][j] = poissonPmf(i, muHome) * poissonPmf(j, muAway);
            }
        }
        return matrix;
    }

    /** Derives 1X2 probabilities by summing the relevant regions of the score matrix. */
    private Probability1X2 derive1X2(final double[][] matrix) {
        double homeWin = 0, draw = 0, awayWin = 0;
        for (int i = 0; i <= MAX_GOALS; i++) {
            for (int j = 0; j <= MAX_GOALS; j++) {
                if      (i > j) homeWin += matrix[i][j];
                else if (i == j) draw   += matrix[i][j];
                else             awayWin += matrix[i][j];
            }
        }
        final Probability1X2 p = new Probability1X2();
        p.setHomeWin(round(homeWin * 100));
        p.setDraw(round(draw * 100));
        p.setAwayWin(round(awayWin * 100));
        return p;
    }

    /** Computes Over/Under 2.5, BTTS, and average total goals from the score matrix. */
    private void calcGoalMarkets(final RiskAnalysis risk, final double[][] matrix,
                                  final double muHome, final double muAway) {
        double over = 0, btts = 0;
        for (int i = 0; i <= MAX_GOALS; i++) {
            for (int j = 0; j <= MAX_GOALS; j++) {
                if (i + j > 2)          over += matrix[i][j];
                if (i > 0 && j > 0)     btts += matrix[i][j];
            }
        }
        risk.setAvgTotalGoals(round(muHome + muAway));
        risk.setOverPercentage(round(over * 100));
        risk.setUnderPercentage(round((1.0 - over) * 100));
        risk.setBttsYesPercentage(round(btts * 100));
        risk.setBttsNoPercentage(round((1.0 - btts) * 100));
    }

    /**
     * Poisson PMF: P(X=k | λ) = e^{-λ} × λ^k / k!.
     * Computed in log-space for numerical stability.
     */
    private double poissonPmf(final int k, final double lambda) {
        if (lambda <= 0) return k == 0 ? 1.0 : 0.0;
        double logP = -lambda + k * Math.log(lambda);
        for (int n = 2; n <= k; n++) logP -= Math.log(n);
        return Math.exp(logP);
    }

    private double round(final double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
