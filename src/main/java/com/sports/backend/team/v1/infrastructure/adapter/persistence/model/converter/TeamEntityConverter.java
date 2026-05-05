package com.sports.backend.team.v1.infrastructure.adapter.persistence.model.converter;

import com.sports.backend.team.v1.application.domain.model.Team;
import com.sports.backend.team.v1.infrastructure.adapter.persistence.model.TeamEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamEntityConverter {

    Team toDomain(TeamEntity entity);

    TeamEntity toEntity(Team domain);

    List<Team> toDomainList(List<TeamEntity> entities);
}
