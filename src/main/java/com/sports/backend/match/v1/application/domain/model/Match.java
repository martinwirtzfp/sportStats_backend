package com.sports.backend.match.v1.application.domain.model;

import lombok.Data;

import java.time.Instant;

@Data
public class Match {

    private Long id;
    private Long homeTeamId;
    private Long awayTeamId;
    private String homeTeamName;
    private String awayTeamName;
    private String homeTeamLogo;
    private String awayTeamLogo;
    private Instant matchDate;
    private String status;
    private Integer homeGoals;
    private Integer awayGoals;
    private Integer htHomeGoals;
    private Integer htAwayGoals;
    private Long competitionId;
    private String competitionName;
    private String season;
    private Long apiId;
}
