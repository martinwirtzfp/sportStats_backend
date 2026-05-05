package com.sports.backend.team.v1.infrastructure.adapter.persistence.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "team")
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "short_name", length = 10)
    private String shortName;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "api_id", unique = true)
    private Integer apiId;

    @Column(name = "competition_id")
    private Long competitionId;
}
