package com.sports.backend.team.v1.infrastructure.adapter.rest.model;

import lombok.Data;

@Data
public class TeamDto {

    private Long id;
    private String name;
    private String shortName;
    private String logoUrl;
    private Integer apiId;
    private Long competitionId;
    private String competitionName;
}
