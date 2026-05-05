package com.sports.backend.match.v1.infrastructure.adapter.persistence.model.converter;

import com.sports.backend.match.v1.application.domain.model.Match;
import com.sports.backend.match.v1.application.domain.model.MatchStatistics;
import com.sports.backend.match.v1.infrastructure.adapter.persistence.model.MatchEntity;
import com.sports.backend.match.v1.infrastructure.adapter.persistence.model.MatchStatisticsEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MatchEntityConverter {

    Match toDomain(MatchEntity entity);

    MatchEntity toEntity(Match domain);

    List<Match> toDomainList(List<MatchEntity> entities);

    MatchStatistics toDomain(MatchStatisticsEntity entity);

    MatchStatisticsEntity toEntity(MatchStatistics domain);
}
