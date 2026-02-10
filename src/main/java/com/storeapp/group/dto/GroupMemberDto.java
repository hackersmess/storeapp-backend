package com.storeapp.group.dto;

import com.storeapp.auth.dto.UserDto;
import com.storeapp.group.entity.GroupRole;

import java.time.LocalDateTime;

/**
 * DTO per rappresentare un membro del gruppo
 *
 * Le conversioni Entity <-> DTO sono gestite da GroupMemberMapper
 */
public class GroupMemberDto {

    public Long id;
    public Long groupId;
    public UserDto user;
    public GroupRole role;
    public LocalDateTime joinedAt;
}
