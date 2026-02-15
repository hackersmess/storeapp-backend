package com.storeapp.activity.dto;

import com.storeapp.group.dto.GroupMemberDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO per la risposta con i dettagli di una spesa
 */
public class ActivityExpenseDto {

    public Long id;
    public Long activityId;
    public String description;
    public BigDecimal amount;
    public GroupMemberDto paidBy;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public List<ActivityExpenseSplitDto> splits;
}
