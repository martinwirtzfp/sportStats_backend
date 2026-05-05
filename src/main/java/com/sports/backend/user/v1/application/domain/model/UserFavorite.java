package com.sports.backend.user.v1.application.domain.model;

import lombok.Data;

@Data
public class UserFavorite {

    private Long id;
    private Long userId;
    private Long teamId;
    private String teamName;
    private String teamLogo;
}
