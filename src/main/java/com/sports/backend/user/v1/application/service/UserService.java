package com.sports.backend.user.v1.application.service;

import com.sports.backend.shared.v1.application.exception.ApplicationException;
import com.sports.backend.shared.v1.application.exception.error.ApplicationError;
import com.sports.backend.team.v1.application.domain.port.TeamPort;
import com.sports.backend.user.v1.application.domain.model.UserFavorite;
import com.sports.backend.user.v1.application.domain.port.UserFavoritePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserFavoritePort favoritePort;
    private final TeamPort teamPort;

    public List<UserFavorite> getFavorites(final Long userId) {
        return favoritePort.findByUserId(userId);
    }

    public UserFavorite addFavorite(final Long userId, final Long teamId) {
        if (favoritePort.findByUserIdAndTeamId(userId, teamId).isPresent()) {
            throw new ApplicationException(ApplicationError.FAVORITE_ALREADY_EXISTS);
        }
        final var team = teamPort.findById(teamId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TEAM_NOT_FOUND));
        final UserFavorite fav = new UserFavorite();
        fav.setUserId(userId);
        fav.setTeamId(teamId);
        fav.setTeamName(team.getName());
        fav.setTeamLogo(team.getLogoUrl());
        return favoritePort.save(fav);
    }

    public void removeFavorite(final Long userId, final Long teamId) {
        final UserFavorite fav = favoritePort.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.RESOURCE_NOT_FOUND));
        favoritePort.delete(fav.getId());
    }
}
