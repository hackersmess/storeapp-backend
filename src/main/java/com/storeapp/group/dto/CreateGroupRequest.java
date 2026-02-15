package com.storeapp.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO per la creazione di un nuovo gruppo
 */
public class CreateGroupRequest {

    @NotBlank(message = "Il nome del gruppo è obbligatorio")
    @Size(min = 3, max = 200, message = "Il nome deve essere tra 3 e 200 caratteri")
    public String name;

    @Size(max = 2000, message = "La descrizione non può superare 2000 caratteri")
    public String description;

    @NotNull(message = "La data di inizio vacanza è obbligatoria")
    public LocalDate vacationStartDate;

    @NotNull(message = "La data di fine vacanza è obbligatoria")
    public LocalDate vacationEndDate;

    @Size(max = 500, message = "URL immagine troppo lungo")
    public String coverImageUrl;

    /**
     * Lista opzionale di membri da aggiungere al gruppo al momento della creazione
     * Se presente, vengono aggiunti in modo atomico nella stessa transazione
     */
    public List<AddMemberRequest> members;
}
