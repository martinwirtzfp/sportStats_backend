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
    private List<Match> recentMatches;
}
