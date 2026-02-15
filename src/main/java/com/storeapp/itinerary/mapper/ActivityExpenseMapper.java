package com.storeapp.itinerary.mapper;

import com.storeapp.group.mapper.GroupMemberMapper;
import com.storeapp.itinerary.dto.ActivityExpenseDto;
import com.storeapp.itinerary.dto.ActivityExpenseRequest;
import com.storeapp.itinerary.entity.ActivityExpense;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mapstruct.*;

import java.util.List;

@ApplicationScoped
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ActivityExpenseMapper {

    @Inject
    protected GroupMemberMapper groupMemberMapper;

    @Inject
    protected ActivityExpenseSplitMapper splitMapper;

    @Mapping(target = "activityId", source = "activity.id")
    @Mapping(target = "paidBy", source = "paidBy")
    @Mapping(target = "splits", source = "splits")
    public abstract ActivityExpenseDto toDto(ActivityExpense expense);

    public abstract List<ActivityExpenseDto> toDtoList(List<ActivityExpense> expenses);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activity", ignore = true)
    @Mapping(target = "paidBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "splits", ignore = true)
    public abstract ActivityExpense toEntity(ActivityExpenseRequest request);
}
