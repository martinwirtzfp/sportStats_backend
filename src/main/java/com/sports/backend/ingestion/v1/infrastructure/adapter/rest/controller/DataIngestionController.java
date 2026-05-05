package com.sports.backend.ingestion.v1.infrastructure.adapter.rest.controller;

import com.sports.backend.ingestion.v1.application.service.DataIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingestion")
@Tag(name = "Data Ingestion", description = "Trigger data ingestion from API-Football (requires authentication)")
@RequiredArgsConstructor
public class DataIngestionController {

    private final DataIngestionService dataIngestionService;

    @Operation(summary = "Ingest teams and matches for a league/season. Creates the competition automatically if it does not exist.")
    @PostMapping("/leagues/{leagueApiId}")
    public ResponseEntity<String> ingestLeague(
            @PathVariable final Integer leagueApiId,
            @RequestParam final String season,
            @RequestParam(defaultValue = "Unknown League") final String competitionName) {
        final String result = dataIngestionService.ingestLeague(leagueApiId, season, competitionName);
        return ResponseEntity.ok(result);
    }
}
