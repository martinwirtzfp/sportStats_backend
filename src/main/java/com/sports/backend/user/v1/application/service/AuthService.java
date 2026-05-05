package com.sports.backend.user.v1.application.service;

import com.sports.backend.config.security.JwtService;
import com.sports.backend.shared.v1.application.exception.ApplicationException;
import com.sports.backend.shared.v1.application.exception.error.ApplicationError;
import com.sports.backend.user.v1.application.domain.model.User;
import com.sports.backend.user.v1.application.domain.port.UserPort;
import com.sports.backend.user.v1.infrastructure.adapter.rest.model.LoginRequestDto;
import com.sports.backend.user.v1.infrastructure.adapter.rest.model.LoginResponseDto;
import com.sports.backend.user.v1.infrastructure.adapter.rest.model.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserPort userPort;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDto register(final RegisterRequestDto request) {
        if (userPort.existsByEmail(request.getEmail())) {
            throw new ApplicationException(ApplicationError.EMAIL_ALREADY_EXISTS);
        }
        final User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(Instant.now());
        final User saved = userPort.save(user);
        final String token = jwtService.generateToken(saved.getId(), saved.getEmail());
        return new LoginResponseDto(token, saved.getId(), saved.getUsername(), saved.getEmail());
    }

    public LoginResponseDto login(final LoginRequestDto request) {
        final User user = userPort.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApplicationException(ApplicationError.INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApplicationException(ApplicationError.INVALID_CREDENTIALS);
        }
        final String token = jwtService.generateToken(user.getId(), user.getEmail());
        return new LoginResponseDto(token, user.getId(), user.getUsername(), user.getEmail());
    }
}
