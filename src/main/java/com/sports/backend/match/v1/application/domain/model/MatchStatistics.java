package com.sports.backend.match.v1.application.domain.model;

import lombok.Data;

@Data
public class MatchStatistics {

    private Long id;
    private Long matchId;
    private Long teamId;
    private Integer shots;
    private Integer shotsOnTarget;
    private Double possession;
    private Integer corners;
    private Integer fouls;
    private Integer yellowCards;
    private Integer redCards;
}
