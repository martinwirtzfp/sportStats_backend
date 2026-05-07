package com.sports.backend.team.v1.infrastructure.adapter.persistence.model;

import com.sports.backend.competition.v1.infrastructure.adapter.persistence.model.CompetitionEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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

    /**
     * Read-only association used solely to enforce the FK constraint at DB level.
     * Business logic uses {@code competitionId} directly to keep modules decoupled.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_team_competition"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CompetitionEntity competition;
}
