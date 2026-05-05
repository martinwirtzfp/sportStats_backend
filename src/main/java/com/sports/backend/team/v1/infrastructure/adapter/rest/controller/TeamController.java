package com.sports.backend.team.v1.infrastructure.adapter.rest.controller;

import com.sports.backend.team.v1.application.service.TeamService;
import com.sports.backend.team.v1.infrastructure.adapter.rest.model.TeamDto;
import com.sports.backend.team.v1.infrastructure.adapter.rest.model.converter.TeamDtoConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@Tag(name = "Teams", description = "Endpoints for managing teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final TeamDtoConverter converter;

    @Operation(summary = "Get all teams, optionally filtered by competition")
    @GetMapping
    public ResponseEntity<List<TeamDto>> findAll(
            @RequestParam(required = false) final Long competitionId) {
        final List<TeamDto> result = competitionId != null
                ? converter.toDtoList(teamService.findByCompetitionId(competitionId))
                : converter.toDtoList(teamService.findAll());
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get team by id")
    @GetMapping("/{id}")
    public ResponseEntity<TeamDto> findById(@PathVariable final Long id) {
        return ResponseEntity.ok(converter.toDto(teamService.findById(id)));
    }
}
