package com.storeapp.activity.mapper;

import com.storeapp.group.mapper.GroupMemberMapper;
import com.storeapp.activity.dto.ActivityExpenseDto;
import com.storeapp.activity.entity.ActivityExpense;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ActivityExpenseMapper {

    @Inject
    GroupMemberMapper groupMemberMapper;

    @Inject
    ActivityExpenseSplitMapper splitMapper;

    public ActivityExpenseDto toDto(ActivityExpense expense) {
        if (expense == null) return null;

        ActivityExpenseDto dto = new ActivityExpenseDto();
        dto.id = expense.id;
        dto.activityId = expense.activity != null ? expense.activity.id : null;
        dto.description = expense.description;
        dto.amount = expense.amount;
        dto.currency = expense.currency != null ? expense.currency : "EUR";
        dto.createdAt = expense.createdAt;
        dto.updatedAt = expense.updatedAt;

        // Payers: collect splits with isPayer=true
        if (expense.splits != null) {
            dto.payers = expense.splits.stream()
                .filter(s -> Boolean.TRUE.equals(s.isPayer))
                .map(s -> groupMemberMapper.toDto(s.groupMember))
                .collect(Collectors.toList());

            dto.splits = splitMapper.toDtoList(new ArrayList<>(expense.splits));
        } else {
            // Fallback: use paidBy as single payer
            dto.payers = expense.paidBy != null
                ? List.of(groupMemberMapper.toDto(expense.paidBy))
                : List.of();
            dto.splits = List.of();
        }

        return dto;
    }

    public List<ActivityExpenseDto> toDtoList(List<ActivityExpense> expenses) {
        if (expenses == null) return List.of();
        return expenses.stream().map(this::toDto).collect(Collectors.toList());
    }
}
