package com.storeapp.activity.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO per la creazione di una spesa con splits e supporto multi-payer
 */
public class ActivityExpenseRequest {

    @NotBlank(message = "La descrizione è obbligatoria")
    @Size(max = 200, message = "La descrizione non può superare 200 caratteri")
    public String description;

    @Size(max = 10)
    public String currency = "EUR";

    @NotEmpty(message = "Almeno un pagante è obbligatorio")
    @Valid
    public List<PayerRequest> payers;

    @Valid
    public List<ExpenseSplitRequest> splits;

    public static class PayerRequest {
        @NotNull(message = "L'ID del membro pagante è obbligatorio")
        public Long groupMemberId;

        @NotNull(message = "L'importo pagato è obbligatorio")
        @Positive(message = "L'importo deve essere positivo")
        public BigDecimal paidAmount;
    }

    public static class ExpenseSplitRequest {
        @NotNull(message = "L'ID del membro è obbligatorio")
        public Long groupMemberId;

        @NotNull(message = "L'importo della split è obbligatorio")
        @Positive(message = "L'importo deve essere positivo")
        public BigDecimal amount;

        public Boolean isPayer = false;
        public BigDecimal paidAmount = BigDecimal.ZERO;
    }
}
