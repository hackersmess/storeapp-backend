package com.storeapp.user.mapper;

import com.storeapp.user.dto.CreateUserRequest;
import com.storeapp.user.dto.UpdateUserRequest;
import com.storeapp.user.dto.UserResponse;
import com.storeapp.user.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct Mapper per conversione User Entity <-> DTO
 */
@ApplicationScoped
@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    /**
     * Converte User entity in UserResponse DTO
     * Esclude passwordHash per sicurezza
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "avatarUrl", source = "avatarUrl")
    @Mapping(target = "bio", source = "bio")
    @Mapping(target = "googleId", source = "googleId")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    UserResponse toUserResponse(User user);

    /**
     * Converte lista di User in lista di UserResponse
     */
    List<UserResponse> toUserResponseList(List<User> users);

    /**
     * Converte CreateUserRequest in User entity
     * Password viene gestita separatamente nel service (hashing)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true) // Gestito nel service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(CreateUserRequest request);

    /**
     * Aggiorna User entity da UpdateUserRequest
     * Solo i campi non-null vengono aggiornati (partial update)
     *
     * @param request dati da aggiornare
     * @param user entity esistente da aggiornare
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true) // Gestito nel service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateUserRequest request, @MappingTarget User user);
}
