package com.storeapp.group.mapper;
import com.storeapp.auth.mapper.UserDtoMapper;
import com.storeapp.group.dto.GroupMemberDto;
import com.storeapp.group.entity.GroupMember;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mapstruct.*;
import java.util.List;

@ApplicationScoped
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class GroupMemberMapper {

    @Inject
    protected UserDtoMapper userDtoMapper;

    @Mapping(target = "groupId", source = "group.id")
    public abstract GroupMemberDto toDto(GroupMember member);

    public abstract List<GroupMemberDto> toDtoList(List<GroupMember> members);
}


