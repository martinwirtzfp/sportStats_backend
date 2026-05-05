package com.sports.backend.competition.v1.infrastructure.adapter.persistence.model.converter;

import com.sports.backend.competition.v1.application.domain.model.Competition;
import com.sports.backend.competition.v1.infrastructure.adapter.persistence.model.CompetitionEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompetitionEntityConverter {

    Competition toDomain(CompetitionEntity entity);

    CompetitionEntity toEntity(Competition domain);

    List<Competition> toDomainList(List<CompetitionEntity> entities);
}
