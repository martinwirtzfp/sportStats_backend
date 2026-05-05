package com.sports.backend.competition.v1.application.domain.model;

import lombok.Data;

@Data
public class Competition {

    private Long id;
    private String name;
    private Integer apiId;
    private String type;
    private String season;
    private String logoUrl;
}
