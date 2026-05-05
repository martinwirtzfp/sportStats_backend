package com.sports.backend.team.v1.infrastructure.adapter.rest.model.converter;

import com.sports.backend.team.v1.application.domain.model.Team;
import com.sports.backend.team.v1.infrastructure.adapter.rest.model.TeamDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamDtoConverter {

    TeamDto toDto(Team domain);

    Team toDomain(TeamDto dto);

    List<TeamDto> toDtoList(List<Team> domains);
}
