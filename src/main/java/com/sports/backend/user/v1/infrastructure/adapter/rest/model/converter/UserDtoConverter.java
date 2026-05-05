package com.sports.backend.user.v1.infrastructure.adapter.rest.model.converter;

import com.sports.backend.user.v1.application.domain.model.UserFavorite;
import com.sports.backend.user.v1.infrastructure.adapter.rest.model.UserFavoriteDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserDtoConverter {

    UserFavoriteDto toDto(UserFavorite domain);

    List<UserFavoriteDto> toDtoList(List<UserFavorite> list);
}
