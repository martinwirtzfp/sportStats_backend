package com.sports.backend.user.v1.infrastructure.adapter.persistence.model.converter;

import com.sports.backend.user.v1.application.domain.model.User;
import com.sports.backend.user.v1.application.domain.model.UserFavorite;
import com.sports.backend.user.v1.infrastructure.adapter.persistence.model.UserEntity;
import com.sports.backend.user.v1.infrastructure.adapter.persistence.model.UserFavoriteEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserEntityConverter {

    User toDomain(UserEntity entity);

    UserEntity toEntity(User domain);

    UserFavorite toDomain(UserFavoriteEntity entity);

    UserFavoriteEntity toEntity(UserFavorite domain);

    List<UserFavorite> toDomainFavoriteList(List<UserFavoriteEntity> entities);
}
