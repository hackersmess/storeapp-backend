package com.storeapp.group.dto;

import com.storeapp.auth.dto.UserDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO per rappresentare un gruppo
 *
 * Le conversioni Entity <-> DTO sono gestite da GroupMapper
 */
public class GroupDto {

    public Long id;
    public String name;
    public String description;
    public LocalDate vacationStartDate;
    public LocalDate vacationEndDate;
    public String coverImageUrl;
    public UserDto createdBy;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public long memberCount;
    public List<GroupMemberDto> members;
}
