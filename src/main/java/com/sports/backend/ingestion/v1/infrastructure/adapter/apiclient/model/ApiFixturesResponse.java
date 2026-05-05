package com.sports.backend.ingestion.v1.infrastructure.adapter.apiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiFixturesResponse {

    @JsonProperty("response")
    private List<FixtureEntry> response;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FixtureEntry {
        @JsonProperty("fixture")
        private FixtureInfo fixture;
        @JsonProperty("league")
        private LeagueInfo league;
        @JsonProperty("teams")
        private TeamsInfo teams;
        @JsonProperty("goals")
        private GoalsInfo goals;
        @JsonProperty("score")
        private ScoreInfo score;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FixtureInfo {
        @JsonProperty("id")
        private Long id;
        @JsonProperty("date")
        private String date;
        @JsonProperty("status")
        private StatusInfo status;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatusInfo {
        @JsonProperty("short")
        private String shortStatus;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeagueInfo {
        @JsonProperty("season")
        private String season;
        @JsonProperty("name")
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamsInfo {
        @JsonProperty("home")
        private TeamRef home;
        @JsonProperty("away")
        private TeamRef away;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamRef {
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("logo")
        private String logo;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GoalsInfo {
        @JsonProperty("home")
        private Integer home;
        @JsonProperty("away")
        private Integer away;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ScoreInfo {
        @JsonProperty("halftime")
        private GoalsInfo halftime;
    }
}
