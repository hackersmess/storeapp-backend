package com.storeapp.itinerary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO per la creazione di una spesa con splits
 */
public class ActivityExpenseRequest {

    @NotBlank(message = "La descrizione è obbligatoria")
    @Size(max = 200, message = "La descrizione non può superare 200 caratteri")
    public String description;

    @NotNull(message = "L'importo è obbligatorio")
    @Positive(message = "L'importo deve essere positivo")
    public BigDecimal amount;

    @NotNull(message = "L'ID di chi ha pagato è obbligatorio")
    public Long paidByGroupMemberId;

    public List<ExpenseSplitRequest> splits;

    public static class ExpenseSplitRequest {
        @NotNull(message = "L'ID del membro è obbligatorio")
        public Long groupMemberId;

        @NotNull(message = "L'importo della split è obbligatorio")
        @Positive(message = "L'importo deve essere positivo")
        public BigDecimal amount;

        public Boolean isSettled = false;
    }
}
