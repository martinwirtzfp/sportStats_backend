package com.sports.backend.team.v1.infrastructure.adapter.persistence.repository;

import com.sports.backend.team.v1.application.domain.model.Team;
import com.sports.backend.team.v1.application.domain.port.TeamPort;
import com.sports.backend.team.v1.infrastructure.adapter.persistence.model.converter.TeamEntityConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TeamRepository implements TeamPort {

    private final TeamJpaRepository jpaRepository;
    private final TeamEntityConverter converter;

    @Override
    public List<Team> findAll() {
        return converter.toDomainList(jpaRepository.findAll());
    }

    @Override
    public List<Team> findByCompetitionId(final Long competitionId) {
        return converter.toDomainList(jpaRepository.findByCompetitionId(competitionId));
    }

    @Override
    public List<Team> findAllByIds(final List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return converter.toDomainList(new ArrayList<>(jpaRepository.findAllById(ids)));
    }

    @Override
    public Optional<Team> findById(final Long id) {
        return jpaRepository.findById(id).map(converter::toDomain);
    }

    @Override
    public Optional<Team> findByApiId(final Integer apiId) {
        return jpaRepository.findByApiId(apiId).map(converter::toDomain);
    }

    @Override
    public Team save(final Team team) {
        return converter.toDomain(jpaRepository.save(converter.toEntity(team)));
    }
}
