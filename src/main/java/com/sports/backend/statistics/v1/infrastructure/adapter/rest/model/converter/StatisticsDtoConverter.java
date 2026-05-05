package com.sports.backend.statistics.v1.infrastructure.adapter.rest.model.converter;

import com.sports.backend.match.v1.infrastructure.adapter.rest.model.MatchDto;
import com.sports.backend.match.v1.infrastructure.adapter.rest.model.converter.MatchDtoConverter;
import com.sports.backend.statistics.v1.application.domain.model.HeadToHead;
import com.sports.backend.statistics.v1.application.domain.model.Probability1X2;
import com.sports.backend.statistics.v1.application.domain.model.RiskAnalysis;
import com.sports.backend.statistics.v1.application.domain.model.TeamStats;
import com.sports.backend.statistics.v1.infrastructure.adapter.rest.model.HeadToHeadDto;
import com.sports.backend.statistics.v1.infrastructure.adapter.rest.model.Probability1X2Dto;
import com.sports.backend.statistics.v1.infrastructure.adapter.rest.model.RiskAnalysisDto;
import com.sports.backend.statistics.v1.infrastructure.adapter.rest.model.TeamStatsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MatchDtoConverter.class})
public interface StatisticsDtoConverter {

    TeamStatsDto toDto(TeamStats domain);

    Probability1X2Dto toDto(Probability1X2 domain);

    RiskAnalysisDto toDto(RiskAnalysis domain);

    @Mapping(source = "recentMatches", target = "recentMatches")
    HeadToHeadDto toDto(HeadToHead domain);
}
