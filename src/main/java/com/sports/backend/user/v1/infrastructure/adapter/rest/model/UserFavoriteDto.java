package com.sports.backend.user.v1.infrastructure.adapter.rest.model;

import lombok.Data;

@Data
public class UserFavoriteDto {
    private Long id;
    private Long teamId;
    private String teamName;
    private String teamLogo;
}
