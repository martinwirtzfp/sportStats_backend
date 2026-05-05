package com.sports.backend.user.v1.infrastructure.adapter.persistence.repository;

import com.sports.backend.user.v1.application.domain.model.UserFavorite;
import com.sports.backend.user.v1.application.domain.port.UserFavoritePort;
import com.sports.backend.user.v1.infrastructure.adapter.persistence.model.converter.UserEntityConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserFavoriteRepository implements UserFavoritePort {

    private final UserFavoriteJpaRepository jpaRepository;
    private final UserEntityConverter converter;

    @Override
    public List<UserFavorite> findByUserId(final Long userId) {
        return converter.toDomainFavoriteList(jpaRepository.findByUserId(userId));
    }

    @Override
    public Optional<UserFavorite> findByUserIdAndTeamId(final Long userId, final Long teamId) {
        return jpaRepository.findByUserIdAndTeamId(userId, teamId).map(converter::toDomain);
    }

    @Override
    public UserFavorite save(final UserFavorite favorite) {
        return converter.toDomain(jpaRepository.save(converter.toEntity(favorite)));
    }

    @Override
    public void delete(final Long id) {
        jpaRepository.deleteById(id);
    }
}
