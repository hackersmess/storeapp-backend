package com.storeapp.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO per la creazione di un nuovo gruppo
 */
public class CreateGroupRequest {

    @NotBlank(message = "Il nome del gruppo è obbligatorio")
    @Size(min = 3, max = 200, message = "Il nome deve essere tra 3 e 200 caratteri")
    public String name;

    @Size(max = 2000, message = "La descrizione non può superare 2000 caratteri")
    public String description;

    public LocalDate vacationStartDate;

    public LocalDate vacationEndDate;

    @Size(max = 500, message = "URL immagine troppo lungo")
    public String coverImageUrl;
}
