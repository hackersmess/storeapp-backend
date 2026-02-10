package com.storeapp.auth.mapper;

import com.storeapp.auth.dto.UserDto;
import com.storeapp.user.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import org.mapstruct.*;

/**
 * MapStruct Mapper per conversione User Entity -> UserDto
 * Mapper condiviso tra tutti i moduli
 */
@ApplicationScoped
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDtoMapper {

    /**
     * Converte User entity in UserDto
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "avatarUrl", source = "avatarUrl")
    @Mapping(target = "bio", source = "bio")
    @Mapping(target = "createdAt", source = "createdAt")
    UserDto toDto(User user);
}
