package com.sports.backend.competition.v1.infrastructure.adapter.persistence.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "competition", uniqueConstraints = @UniqueConstraint(columnNames = {"api_id", "season"}))
public class CompetitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "api_id")
    private Integer apiId;

    @Column(length = 30)
    private String type;

    @Column(length = 10)
    private String season;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;
}
