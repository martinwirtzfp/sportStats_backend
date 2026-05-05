package com.sports.backend.user.v1.infrastructure.adapter.persistence.repository;

import com.sports.backend.user.v1.application.domain.model.User;
import com.sports.backend.user.v1.application.domain.port.UserPort;
import com.sports.backend.user.v1.infrastructure.adapter.persistence.model.converter.UserEntityConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository implements UserPort {

    private final UserJpaRepository jpaRepository;
    private final UserEntityConverter converter;

    @Override
    public Optional<User> findByEmail(final String email) {
        return jpaRepository.findByEmail(email).map(converter::toDomain);
    }

    @Override
    public boolean existsByEmail(final String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public User save(final User user) {
        return converter.toDomain(jpaRepository.save(converter.toEntity(user)));
    }
}
