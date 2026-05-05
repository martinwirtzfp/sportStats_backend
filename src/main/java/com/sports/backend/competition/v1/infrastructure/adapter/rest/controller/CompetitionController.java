package com.sports.backend.competition.v1.infrastructure.adapter.rest.controller;

import com.sports.backend.competition.v1.application.service.CompetitionService;
import com.sports.backend.competition.v1.infrastructure.adapter.rest.model.CompetitionDto;
import com.sports.backend.competition.v1.infrastructure.adapter.rest.model.converter.CompetitionDtoConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
@Tag(name = "Competitions", description = "Endpoints for managing competitions")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;
    private final CompetitionDtoConverter converter;

    @Operation(summary = "Get all competitions")
    @GetMapping
    public ResponseEntity<List<CompetitionDto>> findAll() {
        final List<CompetitionDto> result = converter.toDtoList(competitionService.findAll());
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get competition by id")
    @GetMapping("/{id}")
    public ResponseEntity<CompetitionDto> findById(@PathVariable final Long id) {
        return ResponseEntity.ok(converter.toDto(competitionService.findById(id)));
    }
}
