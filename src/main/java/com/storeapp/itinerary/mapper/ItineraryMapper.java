package com.storeapp.itinerary.mapper;

import com.storeapp.itinerary.dto.ItineraryDto;
import com.storeapp.itinerary.dto.ItineraryRequest;
import com.storeapp.itinerary.entity.Itinerary;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mapstruct.*;

import java.util.List;

@ApplicationScoped
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ItineraryMapper {

    @Inject
    protected ActivityMapper activityMapper;

    @Named("toDto")
    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "activityCount", expression = "java(itinerary.getActivityCount())")
    @Mapping(target = "completedActivitiesCount", expression = "java(itinerary.getCompletedActivitiesCount())")
    @Mapping(target = "activities", ignore = true)
    public abstract ItineraryDto toDto(Itinerary itinerary);

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "activityCount", expression = "java(itinerary.getActivityCount())")
    @Mapping(target = "completedActivitiesCount", expression = "java(itinerary.getCompletedActivitiesCount())")
    @Mapping(target = "activities", source = "activities")
    public abstract ItineraryDto toDtoWithActivities(Itinerary itinerary);

    @IterableMapping(qualifiedByName = "toDto")
    public abstract List<ItineraryDto> toDtoList(List<Itinerary> itineraries);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "activities", ignore = true)
    public abstract Itinerary toEntity(ItineraryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "activities", ignore = true)
    public abstract void updateEntityFromRequest(ItineraryRequest request, @MappingTarget Itinerary itinerary);
}
