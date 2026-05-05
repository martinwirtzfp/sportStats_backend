package com.sports.backend.user.v1.application.domain.model;

import lombok.Data;

import java.time.Instant;

@Data
public class User {

    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private Instant createdAt;
}
