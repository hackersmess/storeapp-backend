package com.storeapp.group.dto;

import com.storeapp.group.entity.GroupRole;
import jakarta.validation.constraints.NotNull;

/**
 * DTO per cambiare il ruolo di un membro
 */
public class UpdateMemberRoleRequest {

    @NotNull(message = "Il ruolo è obbligatorio")
    public GroupRole role;
}
