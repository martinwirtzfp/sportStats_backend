package com.sports.backend.user.v1.infrastructure.adapter.persistence.model;

import jakarta.persistence.*;
import lombok.Data;

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
}
