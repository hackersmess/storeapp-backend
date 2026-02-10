package com.storeapp.group.mapper;
import com.storeapp.auth.mapper.UserDtoMapper;
import com.storeapp.group.dto.*;
import com.storeapp.group.entity.Group;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mapstruct.*;
import java.util.List;

@ApplicationScoped
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class GroupMapper {

    @Inject
    protected GroupMemberMapper groupMemberMapper;

    @Inject
    protected UserDtoMapper userDtoMapper;

    @Mapping(target = "memberCount", expression = "java(group.getMemberCount())")
    @Mapping(target = "members", ignore = true)
    public abstract GroupDto toDto(Group group);

    @Mapping(target = "memberCount", expression = "java(group.getMemberCount())")
    @Mapping(target = "members", source = "members")
    public abstract GroupDto toDtoWithMembers(Group group);

    @IterableMapping(qualifiedByName = "toDto")
    public abstract List<GroupDto> toDtoList(List<Group> groups);

    @Named("toDto")
    @Mapping(target = "memberCount", expression = "java(group.getMemberCount())")
    @Mapping(target = "members", ignore = true)
    public abstract GroupDto toDtoBasic(Group group);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "members", ignore = true)
    public abstract Group toEntity(CreateGroupRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "members", ignore = true)
    public abstract void updateEntityFromRequest(UpdateGroupRequest request, @MappingTarget Group group);
}
