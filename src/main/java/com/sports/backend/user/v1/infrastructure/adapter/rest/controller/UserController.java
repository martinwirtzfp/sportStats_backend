package com.sports.backend.user.v1.infrastructure.adapter.rest.controller;

import com.sports.backend.config.security.JwtService;
import com.sports.backend.user.v1.application.service.UserService;
import com.sports.backend.user.v1.infrastructure.adapter.rest.model.UserFavoriteDto;
import com.sports.backend.user.v1.infrastructure.adapter.rest.model.converter.UserDtoConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/me")
@Tag(name = "Users", description = "User favorites management (requires JWT)")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserDtoConverter converter;

    @Operation(summary = "Get current user's favorite teams")
    @GetMapping("/favorites")
    public ResponseEntity<List<UserFavoriteDto>> getFavorites(final HttpServletRequest request) {
        final Long userId = extractUserId(request);
        return ResponseEntity.ok(converter.toDtoList(userService.getFavorites(userId)));
    }

    @Operation(summary = "Add a team to favorites")
    @PostMapping("/favorites/{teamId}")
    public ResponseEntity<UserFavoriteDto> addFavorite(
            @PathVariable final Long teamId,
            final HttpServletRequest request) {
        final Long userId = extractUserId(request);
        return ResponseEntity.ok(converter.toDto(userService.addFavorite(userId, teamId)));
    }

    @Operation(summary = "Remove a team from favorites")
    @DeleteMapping("/favorites/{teamId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable final Long teamId,
            final HttpServletRequest request) {
        final Long userId = extractUserId(request);
        userService.removeFavorite(userId, teamId);
        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(final HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String token = authHeader.substring(7);
        return jwtService.extractUserId(token);
    }
}
