package com.sports.backend.team.v1.application.domain.model;

import lombok.Data;

@Data
public class Team {

    private Long id;
    private String name;
    private String shortName;
    private String logoUrl;
    private Integer apiId;
    private Long competitionId;
    private String competitionName;
}
