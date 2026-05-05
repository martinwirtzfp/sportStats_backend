package com.sports.backend.statistics.v1.infrastructure.adapter.rest.controller;

import com.sports.backend.statistics.v1.application.service.HeadToHeadService;
import com.sports.backend.statistics.v1.application.service.RiskCalculationService;
import com.sports.backend.statistics.v1.application.service.StatisticsService;
import com.sports.backend.statistics.v1.infrastructure.adapter.rest.model.HeadToHeadDto;
import com.sports.backend.statistics.v1.infrastructure.adapter.rest.model.RiskAnalysisDto;
import com.sports.backend.statistics.v1.infrastructure.adapter.rest.model.TeamStatsDto;
import com.sports.backend.statistics.v1.infrastructure.adapter.rest.model.converter.StatisticsDtoConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Statistics", description = "Statistics, risk analysis and head-to-head endpoints")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final RiskCalculationService riskCalculationService;
    private final HeadToHeadService headToHeadService;
    private final StatisticsDtoConverter converter;

    @Operation(summary = "Get team statistics for last N matches. Optional season filter (e.g. '2024').")
    @GetMapping("/api/statistics/teams/{teamId}")
    public ResponseEntity<TeamStatsDto> getTeamStats(
            @PathVariable final Long teamId,
            @RequestParam(defaultValue = "10") final int lastN,
            @RequestParam(required = false) final String season) {
        return ResponseEntity.ok(converter.toDto(statisticsService.getTeamStats(teamId, lastN, season)));
    }

    @Operation(summary = "Calculate risk probabilities for a match (1X2, Over/Under, BTTS, Half-time). Optional season filter.")
    @GetMapping("/api/risk")
    public ResponseEntity<RiskAnalysisDto> calculateRisk(
            @RequestParam final Long homeTeamId,
            @RequestParam final Long awayTeamId,
            @RequestParam(defaultValue = "10") final int lastN,
            @RequestParam(required = false) final String season) {
        return ResponseEntity.ok(converter.toDto(riskCalculationService.calculate(homeTeamId, awayTeamId, lastN, season)));
    }

    @Operation(summary = "Get head-to-head record between two teams. Optional season filter.")
    @GetMapping("/api/h2h")
    public ResponseEntity<HeadToHeadDto> getHeadToHead(
            @RequestParam final Long team1Id,
            @RequestParam final Long team2Id,
            @RequestParam(required = false) final String season) {
        return ResponseEntity.ok(converter.toDto(headToHeadService.getHeadToHead(team1Id, team2Id, season)));
    }
}
