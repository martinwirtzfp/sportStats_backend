package com.sports.backend.user.v1.infrastructure.adapter.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private Long userId;
    private String username;
    private String email;
}
