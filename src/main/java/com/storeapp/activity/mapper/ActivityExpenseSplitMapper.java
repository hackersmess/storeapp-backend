package com.storeapp.activity.mapper;

import com.storeapp.group.mapper.GroupMemberMapper;
import com.storeapp.activity.dto.ActivityExpenseSplitDto;
import com.storeapp.activity.entity.ActivityExpenseSplit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mapstruct.*;

import java.util.List;

@ApplicationScoped
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ActivityExpenseSplitMapper {

    @Inject
    protected GroupMemberMapper groupMemberMapper;

    @Mapping(target = "expenseId", source = "expense.id")
    @Mapping(target = "groupMember", source = "groupMember")
    public abstract ActivityExpenseSplitDto toDto(ActivityExpenseSplit split);

    public abstract List<ActivityExpenseSplitDto> toDtoList(List<ActivityExpenseSplit> splits);
}
