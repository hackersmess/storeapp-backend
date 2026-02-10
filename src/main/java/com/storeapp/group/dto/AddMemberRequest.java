package com.storeapp.group.dto;

import com.storeapp.group.entity.GroupRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

/**
 * DTO per aggiungere un membro a un gruppo
 */
public class AddMemberRequest {

    @Email(message = "Email non valida")
    public String email;

    public String username;

    @NotNull(message = "Il ruolo è obbligatorio")
    public GroupRole role = GroupRole.MEMBER;

    // Almeno uno tra email e username deve essere presente
    public boolean isValid() {
        return (email != null && !email.isBlank()) || 
               (username != null && !username.isBlank());
    }
}
