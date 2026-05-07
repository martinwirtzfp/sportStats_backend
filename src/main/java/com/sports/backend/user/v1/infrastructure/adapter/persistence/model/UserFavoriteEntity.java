package com.sports.backend.user.v1.infrastructure.adapter.persistence.model;

import com.sports.backend.team.v1.infrastructure.adapter.persistence.model.TeamEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "user_favorite", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "team_id"}))
public class UserFavoriteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "team_name", length = 100)
    private String teamName;

    @Column(name = "team_logo", length = 500)
    private String teamLogo;

    /**
     * Read-only associations used solely to enforce FK constraints at DB level.
     * Business logic uses {@code userId} and {@code teamId} directly.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_favorite_user"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_favorite_team"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TeamEntity team;
}
