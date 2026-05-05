package com.sports.backend.user.v1.infrastructure.adapter.rest.model;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
