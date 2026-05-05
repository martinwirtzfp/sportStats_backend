package com.sports.backend.config;

import com.sports.backend.competition.v1.application.domain.model.Competition;
import com.sports.backend.competition.v1.application.domain.port.CompetitionPort;
import com.sports.backend.ingestion.v1.infrastructure.adapter.json.JsonDataLoaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@Profile("json")
@RequiredArgsConstructor
@Slf4j
public class JsonDataLoaderRunner implements CommandLineRunner {

    private final JsonDataLoaderService loaderService;
    private final CompetitionPort competitionPort;

    @Override
    public void run(final String... args) throws Exception {
        log.info("=== JSON Data Loader starting ===");

        final Long laligaId = ensureCompetition(140, "La Liga", "League", "2024",
                "https://media.api-sports.io/football/leagues/140.png");

        loaderService.loadTeams(new ClassPathResource("fixtures/laliga_teams.json"), laligaId);
        loaderService.loadFixtures(new ClassPathResource("fixtures/laliga_fixtures.json"), laligaId, "2024");

        log.info("=== JSON Data Loader finished ===");
    }

    private Long ensureCompetition(final Integer apiId, final String name, final String type,
                                    final String season, final String logoUrl) {
        return competitionPort.findByApiId(apiId)
                .map(c -> c.getId())
                .orElseGet(() -> {
                    final Competition comp = new Competition();
                    comp.setApiId(apiId);
                    comp.setName(name);
                    comp.setType(type);
                    comp.setSeason(season);
                    comp.setLogoUrl(logoUrl);
                    return competitionPort.save(comp).getId();
                });
    }
}
