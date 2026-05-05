package com.sports.backend.statistics.v1.application.domain.model;

import lombok.Data;

@Data
public class RiskAnalysis {

    private Long homeTeamId;
    private String homeTeamName;
    private Long awayTeamId;
    private String awayTeamName;
    private Integer lastN;

    // 1X2
    private Probability1X2 probability1X2;

    // Over/Under 2.5
    private Double overPercentage;
    private Double underPercentage;
    private Double avgTotalGoals;

    // BTTS
    private Double bttsYesPercentage;
    private Double bttsNoPercentage;

    // Half-time result
    private Probability1X2 halfTimeProbability;
}
