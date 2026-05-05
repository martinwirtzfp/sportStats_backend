package com.sports.backend.statistics.v1.infrastructure.adapter.rest.model;

import lombok.Data;

@Data
public class RiskAnalysisDto {

    private Long homeTeamId;
    private String homeTeamName;
    private Long awayTeamId;
    private String awayTeamName;
    private Integer lastN;

    private Probability1X2Dto probability1X2;
    private Double overPercentage;
    private Double underPercentage;
    private Double avgTotalGoals;
    private Double bttsYesPercentage;
    private Double bttsNoPercentage;
    private Probability1X2Dto halfTimeProbability;
}
