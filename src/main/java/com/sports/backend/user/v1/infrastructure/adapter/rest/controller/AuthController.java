package com.sports.backend.user.v1.infrastructure.adapter.rest.controller;

import com.sports.backend.user.v1.application.service.AuthService;
import com.sports.backend.user.v1.infrastructure.adapter.rest.model.LoginRequestDto;
import com.sports.backend.user.v1.infrastructure.adapter.rest.model.LoginResponseDto;
import com.sports.backend.user.v1.infrastructure.adapter.rest.model.RegisterRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register and login endpoints")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(@RequestBody final RegisterRequestDto request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Login and receive JWT token")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody final LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
