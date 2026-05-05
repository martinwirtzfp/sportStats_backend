package com.sports.backend.shared.v1.application.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApplicationError {

    // Generic
    INVALID_PARAMETERS("Invalid or missing parameters", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("Resource not found", HttpStatus.NOT_FOUND),
    INTERNAL_ERROR("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),

    // Auth
    INVALID_CREDENTIALS("Invalid email or password", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("JWT token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("JWT token is invalid", HttpStatus.UNAUTHORIZED),
    EMAIL_ALREADY_EXISTS("Email already registered", HttpStatus.CONFLICT),
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),

    // Domain
    COMPETITION_NOT_FOUND("Competition not found", HttpStatus.NOT_FOUND),
    TEAM_NOT_FOUND("Team not found", HttpStatus.NOT_FOUND),
    MATCH_NOT_FOUND("Match not found", HttpStatus.NOT_FOUND),
    FAVORITE_ALREADY_EXISTS("Team is already in favorites", HttpStatus.CONFLICT),

    // Ingestion
    APIFOOTBALL_ERROR("Error calling API-Football", HttpStatus.BAD_GATEWAY);

    private final String message;
    private final HttpStatus httpStatus;
}
