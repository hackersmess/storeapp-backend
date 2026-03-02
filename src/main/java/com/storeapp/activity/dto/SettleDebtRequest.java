package com.storeapp.activity.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO per registrare un pagamento di saldo tra due membri del gruppo.
 * Il debitore (fromMemberId) paga il creditore (toMemberId) l'importo indicato.
 */
public class SettleDebtRequest {

    /** GroupMember.id del membro che paga (debitore) */
    @NotNull(message = "Il membro pagante è obbligatorio")
    public Long fromMemberId;

    /** GroupMember.id del membro che riceve (creditore) */
    @NotNull(message = "Il membro ricevente è obbligatorio")
    public Long toMemberId;

    /** Importo del rimborso */
    @NotNull(message = "L'importo è obbligatorio")
    @Positive(message = "L'importo deve essere positivo")
    public BigDecimal amount;

    /** Valuta (default EUR) */
    public String currency = "EUR";

    /** Nota opzionale (es. "Bonifico 02/03/2026") */
    public String note;
}
