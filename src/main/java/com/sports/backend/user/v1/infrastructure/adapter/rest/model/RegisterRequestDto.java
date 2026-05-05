package com.sports.backend.user.v1.infrastructure.adapter.rest.model;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String username;
    private String email;
    private String password;
}
