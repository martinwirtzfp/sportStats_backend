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

    @Operation(summary = "Get all teams. Filter by competition and optionally by season (derived from match history).")
    @GetMapping
    public ResponseEntity<List<TeamDto>> findAll(
            @RequestParam(required = false) final Long competitionId,
            @RequestParam(required = false) final String season) {
        final List<TeamDto> result;
        if (competitionId != null && season != null) {
            result = converter.toDtoList(teamService.findByCompetitionAndSeason(competitionId, season));
        } else if (competitionId != null) {
            result = converter.toDtoList(teamService.findByCompetitionId(competitionId));
        } else {
            result = converter.toDtoList(teamService.findAll());
        }
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
