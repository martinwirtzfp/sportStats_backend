package com.sports.backend.competition.v1.infrastructure.adapter.rest.model;

import lombok.Data;

@Data
public class CompetitionDto {

    private Long id;
    private String name;
    private Integer apiId;
    private String type;
    private String season;
    private String logoUrl;
}
