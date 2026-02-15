package com.storeapp.activity.dto;

import com.storeapp.group.dto.GroupMemberDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO per la risposta con i dettagli di una split di spesa
 */
public class ActivityExpenseSplitDto {

    public Long id;
    public Long expenseId;
    public GroupMemberDto groupMember;
    public BigDecimal amount;
    public Boolean isPaid;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
