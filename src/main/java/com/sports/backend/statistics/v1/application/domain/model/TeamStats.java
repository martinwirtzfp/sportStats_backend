package com.sports.backend.statistics.v1.application.domain.model;

import lombok.Data;

@Data
public class TeamStats {

    private Long teamId;
    private String teamName;
    private Integer lastN;
    private Integer totalMatches;
    private Integer wins;
    private Integer draws;
    private Integer losses;
    private Integer goalsScored;
    private Integer goalsConceded;
    private Integer cleanSheets;
    private Double winPercentage;
    private Double drawPercentage;
    private Double lossPercentage;
    private Double goalsScoredAvg;
    private Double goalsConcededAvg;
    private Integer homeWins;
    private Integer homeDraws;
    private Integer homeLosses;
    private Integer awayWins;
    private Integer awayDraws;
    private Integer awayLosses;
}
