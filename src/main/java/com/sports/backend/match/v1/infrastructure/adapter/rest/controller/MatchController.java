package com.sports.backend.match.v1.infrastructure.adapter.rest.controller;

import com.sports.backend.match.v1.application.service.MatchService;
import com.sports.backend.match.v1.infrastructure.adapter.rest.model.MatchDto;
import com.sports.backend.match.v1.infrastructure.adapter.rest.model.converter.MatchDtoConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@Tag(name = "Matches", description = "Endpoints for querying match data")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final MatchDtoConverter converter;

    @Operation(summary = "Get match by id")
    @GetMapping("/{id}")
    public ResponseEntity<MatchDto> findById(@PathVariable final Long id) {
        return ResponseEntity.ok(converter.toDto(matchService.findById(id)));
    }

    @Operation(summary = "Get last N matches for a team")
    @GetMapping("/teams/{teamId}")
    public ResponseEntity<List<MatchDto>> findLastMatchesByTeam(
            @PathVariable final Long teamId,
            @RequestParam(defaultValue = "10") final int lastN) {
        final List<MatchDto> result = converter.toDtoList(matchService.findLastMatchesByTeam(teamId, lastN));
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get head-to-head matches between two teams")
    @GetMapping("/h2h")
    public ResponseEntity<List<MatchDto>> findHeadToHead(
            @RequestParam final Long team1Id,
            @RequestParam final Long team2Id) {
        final List<MatchDto> result = converter.toDtoList(matchService.findHeadToHead(team1Id, team2Id));
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }
}
