package com.sports.backend.ingestion.v1.infrastructure.adapter.apiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiTeamsResponse {

    @JsonProperty("response")
    private List<TeamEntry> response;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamEntry {
        @JsonProperty("team")
        private TeamInfo team;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamInfo {
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("code")
        private String code;
        @JsonProperty("logo")
        private String logo;
    }
}
