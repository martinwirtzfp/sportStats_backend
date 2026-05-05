package com.sports.backend.match.v1.infrastructure.adapter.persistence.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "match_statistics")
public class MatchStatisticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "match_id", nullable = false)
    private Long matchId;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    private Integer shots;

    @Column(name = "shots_on_target")
    private Integer shotsOnTarget;

    private Double possession;

    private Integer corners;

    private Integer fouls;

    @Column(name = "yellow_cards")
    private Integer yellowCards;

    @Column(name = "red_cards")
    private Integer redCards;
}
