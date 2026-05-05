package com.sports.backend.user.v1.application.domain.port;

import com.sports.backend.user.v1.application.domain.model.User;

import java.util.Optional;

public interface UserPort {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User save(User user);
}
