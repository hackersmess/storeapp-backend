package com.storeapp.itinerary.mapper;

import com.storeapp.itinerary.dto.ActivityDto;
import com.storeapp.itinerary.dto.ActivityRequest;
import com.storeapp.itinerary.entity.Activity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mapstruct.*;

import java.util.List;

@ApplicationScoped
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ActivityMapper {

    @Inject
    protected ActivityParticipantMapper participantMapper;

    @Inject
    protected ActivityExpenseMapper expenseMapper;

    @Named("toDto")
    @Mapping(target = "itineraryId", source = "itinerary.id")
    @Mapping(target = "confirmedCount", expression = "java(activity.getConfirmedCount())")
    @Mapping(target = "maybeCount", expression = "java(activity.getMaybeCount())")
    @Mapping(target = "declinedCount", expression = "java(activity.getDeclinedCount())")
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    public abstract ActivityDto toDto(Activity activity);

    @Mapping(target = "itineraryId", source = "itinerary.id")
    @Mapping(target = "confirmedCount", expression = "java(activity.getConfirmedCount())")
    @Mapping(target = "maybeCount", expression = "java(activity.getMaybeCount())")
    @Mapping(target = "declinedCount", expression = "java(activity.getDeclinedCount())")
    @Mapping(target = "participants", source = "participants")
    @Mapping(target = "expenses", source = "expenses")
    public abstract ActivityDto toDtoWithDetails(Activity activity);

    @IterableMapping(qualifiedByName = "toDto")
    public abstract List<ActivityDto> toDtoList(List<Activity> activities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itinerary", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    public abstract Activity toEntity(ActivityRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itinerary", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    public abstract void updateEntityFromRequest(ActivityRequest request, @MappingTarget Activity activity);
}
