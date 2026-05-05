package com.sports.backend.match.v1.infrastructure.adapter.rest.model.converter;

import com.sports.backend.match.v1.application.domain.model.Match;
import com.sports.backend.match.v1.infrastructure.adapter.rest.model.MatchDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MatchDtoConverter {

    MatchDto toDto(Match domain);

    Match toDomain(MatchDto dto);

    List<MatchDto> toDtoList(List<Match> domains);
}
