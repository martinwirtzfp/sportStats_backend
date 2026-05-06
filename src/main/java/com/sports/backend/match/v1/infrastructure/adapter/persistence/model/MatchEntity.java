package com.sports.backend.match.v1.infrastructure.adapter.persistence.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "match_fixture")
public class MatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "home_team_id", nullable = false)
    private Long homeTeamId;

    @Column(name = "away_team_id", nullable = false)
    private Long awayTeamId;

    @Column(name = "home_team_name", length = 100)
    private String homeTeamName;

    @Column(name = "away_team_name", length = 100)
    private String awayTeamName;

    @Column(name = "home_team_logo", length = 500)
    private String homeTeamLogo;

    @Column(name = "away_team_logo", length = 500)
    private String awayTeamLogo;

    @Column(name = "match_date", columnDefinition = "datetime")
    private Instant matchDate;

    @Column(length = 20)
    private String status;

    @Column(name = "home_goals")
    private Integer homeGoals;

    @Column(name = "away_goals")
    private Integer awayGoals;

    @Column(name = "ht_home_goals")
    private Integer htHomeGoals;

    @Column(name = "ht_away_goals")
    private Integer htAwayGoals;

    @Column(name = "competition_id")
    private Long competitionId;

    @Column(name = "competition_name", length = 100)
    private String competitionName;

    @Column(length = 10)
    private String season;

    @Column(name = "api_id", unique = true)
    private Long apiId;
}
