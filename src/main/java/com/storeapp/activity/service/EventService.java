package com.storeapp.activity.service;

import com.storeapp.activity.dto.EventDto;
import com.storeapp.activity.dto.EventRequest;
import com.storeapp.activity.entity.Event;
import com.storeapp.activity.exception.ActivityNotFoundException;
import com.storeapp.activity.mapper.EventMapper;
import com.storeapp.activity.repository.ActivityRepository;
import com.storeapp.group.entity.Group;
import com.storeapp.group.repository.GroupRepository;
import com.storeapp.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Event-specific operations
 * Events are single-location activities (restaurants, museums, hotels, etc.)
 */
@ApplicationScoped
public class EventService {

    @Inject
    ActivityRepository activityRepository;

    @Inject
    GroupRepository groupRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    EventMapper eventMapper;

    /**
     * Create a new Event activity
     */
    @Transactional
    public EventDto createEvent(Long groupId, EventRequest request, Long userId) {
        // Validate group exists and user is member
        Group group = groupRepository.findByIdWithMembers(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));

        if (!group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        // Create Event entity
        Event event = eventMapper.toEntity(request);
        event.group = group;
        
        // Set creator
        userRepository.findById(userId).ifPresent(user -> event.createdBy = user);

        // Persist
        activityRepository.persist(event);

        return eventMapper.toDto(event);
    }

    /**
     * Get Event by ID
     */
    public EventDto getEvent(Long eventId, Long userId) {
        Event event = (Event) activityRepository.findByIdOptional(eventId)
                .filter(a -> a instanceof Event)
                .orElseThrow(() -> new ActivityNotFoundException(eventId));

        if (!event.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        return eventMapper.toDto(event);
    }

    /**
     * Update Event
     */
    @Transactional
    public EventDto updateEvent(Long eventId, EventRequest request, Long userId) {
        Event event = (Event) activityRepository.findByIdOptional(eventId)
                .filter(a -> a instanceof Event)
                .orElseThrow(() -> new ActivityNotFoundException(eventId));

        if (!event.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        // Update fields
        eventMapper.updateEntityFromRequest(request, event);
        event.updatedAt = LocalDateTime.now();

        return eventMapper.toDto(event);
    }

    /**
     * Delete Event
     */
    @Transactional
    public void deleteEvent(Long eventId, Long userId) {
        Event event = (Event) activityRepository.findByIdOptional(eventId)
                .filter(a -> a instanceof Event)
                .orElseThrow(() -> new ActivityNotFoundException(eventId));

        if (!event.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        activityRepository.delete(event);
    }

    /**
     * Get all Events for a group
     */
    public List<EventDto> getGroupEvents(Long groupId, Long userId) {
        Group group = groupRepository.findByIdOptional(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));

        if (!group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        return activityRepository.findByGroupId(groupId).stream()
                .filter(a -> a instanceof Event)
                .map(a -> eventMapper.toDto((Event) a))
                .collect(Collectors.toList());
    }
}
