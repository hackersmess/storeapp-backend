package com.storeapp.activity.mapper;

import com.storeapp.group.mapper.GroupMemberMapper;
import com.storeapp.activity.dto.ActivityParticipantDto;
import com.storeapp.activity.dto.ActivityParticipantRequest;
import com.storeapp.activity.entity.ActivityParticipant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mapstruct.*;

import java.util.List;

@ApplicationScoped
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ActivityParticipantMapper {

    @Inject
    protected GroupMemberMapper groupMemberMapper;

    @Mapping(target = "activityId", source = "activity.id")
    @Mapping(target = "groupMember", source = "groupMember")
    public abstract ActivityParticipantDto toDto(ActivityParticipant participant);

    public abstract List<ActivityParticipantDto> toDtoList(List<ActivityParticipant> participants);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activity", ignore = true)
    @Mapping(target = "groupMember", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract ActivityParticipant toEntity(ActivityParticipantRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activity", ignore = true)
    @Mapping(target = "groupMember", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract void updateEntityFromRequest(ActivityParticipantRequest request, @MappingTarget ActivityParticipant participant);
}
