package com.sports.backend.statistics.v1.application.domain.model;

import com.sports.backend.match.v1.application.domain.model.Match;
import lombok.Data;

import java.util.List;

@Data
public class HeadToHead {

    private Long team1Id;
    private String team1Name;
    private Long team2Id;
    private String team2Name;
    private Integer totalMatches;
    private Integer team1Wins;
    private Integer draws;
    private Integer team2Wins;
    private Double team1GoalsAvg;
    private Double team2GoalsAvg;
    private Double avgTotalGoals;
    private Integer bttsCount;
    private Double bttsPercentage;

    // Over/Under 2.5
    private Integer overCount;
    private Double overPercentage;
    private Double underPercentage;

    // Half-time results
    private Integer htTeam1Wins;
    private Integer htDraws;
    private Integer htTeam2Wins;
    private Integer htMatchesWithData;

    // Clean sheets in H2H
    private Integer team1CleanSheets;
    private Integer team2CleanSheets;

    // Most repeated exact score (from team1's perspective, e.g. "1-0")
    private String mostCommonScore;
    private Integer mostCommonScoreCount;

    private List<Match> recentMatches;
}
