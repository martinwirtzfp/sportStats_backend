package com.sports.backend.competition.v1.infrastructure.adapter.persistence.repository;

import com.sports.backend.competition.v1.application.domain.model.Competition;
import com.sports.backend.competition.v1.application.domain.port.CompetitionPort;
import com.sports.backend.competition.v1.infrastructure.adapter.persistence.model.converter.CompetitionEntityConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CompetitionRepository implements CompetitionPort {

    private final CompetitionJpaRepository jpaRepository;
    private final CompetitionEntityConverter converter;

    @Override
    public List<Competition> findAll() {
        return converter.toDomainList(jpaRepository.findAll());
    }

    @Override
    public Optional<Competition> findById(final Long id) {
        return jpaRepository.findById(id).map(converter::toDomain);
    }

    @Override
    public Optional<Competition> findByApiId(final Integer apiId) {
        return jpaRepository.findByApiId(apiId).map(converter::toDomain);
    }

    @Override
    public Competition save(final Competition competition) {
        return converter.toDomain(jpaRepository.save(converter.toEntity(competition)));
    }
}
