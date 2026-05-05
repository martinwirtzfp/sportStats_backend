package com.sports.backend.user.v1.application.domain.port;

import com.sports.backend.user.v1.application.domain.model.UserFavorite;

import java.util.List;
import java.util.Optional;

public interface UserFavoritePort {

    List<UserFavorite> findByUserId(Long userId);

    Optional<UserFavorite> findByUserIdAndTeamId(Long userId, Long teamId);

    UserFavorite save(UserFavorite favorite);

    void delete(Long id);
}
