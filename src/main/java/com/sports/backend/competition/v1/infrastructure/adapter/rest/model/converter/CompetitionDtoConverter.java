package com.sports.backend.competition.v1.infrastructure.adapter.rest.model.converter;

import com.sports.backend.competition.v1.application.domain.model.Competition;
import com.sports.backend.competition.v1.infrastructure.adapter.rest.model.CompetitionDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompetitionDtoConverter {

    CompetitionDto toDto(Competition domain);

    Competition toDomain(CompetitionDto dto);

    List<CompetitionDto> toDtoList(List<Competition> domains);
}
