package com.storeapp.activity.service;

import com.storeapp.activity.dto.TripDto;
import com.storeapp.activity.dto.TripRequest;
import com.storeapp.activity.entity.Trip;
import com.storeapp.activity.exception.ActivityNotFoundException;
import com.storeapp.activity.mapper.TripMapper;
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
 * Service for Trip-specific operations
 * Trips are travel activities with origin and destination (flights, trains, car trips, etc.)
 */
@ApplicationScoped
public class TripService {

    @Inject
    ActivityRepository activityRepository;

    @Inject
    GroupRepository groupRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    TripMapper tripMapper;

    /**
     * Create a new Trip activity
     */
    @Transactional
    public TripDto createTrip(Long groupId, TripRequest request, Long userId) {
        // Validate group exists and user is member
        Group group = groupRepository.findByIdWithMembers(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));

        if (!group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        // Create Trip entity
        Trip trip = tripMapper.toEntity(request);
        trip.group = group;
        
        // Set creator
        userRepository.findById(userId).ifPresent(user -> trip.createdBy = user);

        // Persist
        activityRepository.persist(trip);

        return tripMapper.toDto(trip);
    }

    /**
     * Get Trip by ID
     */
    public TripDto getTrip(Long tripId, Long userId) {
        Trip trip = (Trip) activityRepository.findByIdOptional(tripId)
                .filter(a -> a instanceof Trip)
                .orElseThrow(() -> new ActivityNotFoundException(tripId));

        if (!trip.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        return tripMapper.toDto(trip);
    }

    /**
     * Update Trip
     */
    @Transactional
    public TripDto updateTrip(Long tripId, TripRequest request, Long userId) {
        Trip trip = (Trip) activityRepository.findByIdOptional(tripId)
                .filter(a -> a instanceof Trip)
                .orElseThrow(() -> new ActivityNotFoundException(tripId));

        if (!trip.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        // Update fields
        tripMapper.updateEntityFromRequest(request, trip);
        trip.updatedAt = LocalDateTime.now();

        return tripMapper.toDto(trip);
    }

    /**
     * Delete Trip
     */
    @Transactional
    public void deleteTrip(Long tripId, Long userId) {
        Trip trip = (Trip) activityRepository.findByIdOptional(tripId)
                .filter(a -> a instanceof Trip)
                .orElseThrow(() -> new ActivityNotFoundException(tripId));

        if (!trip.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        activityRepository.delete(trip);
    }

    /**
     * Get all Trips for a group
     */
    public List<TripDto> getGroupTrips(Long groupId, Long userId) {
        Group group = groupRepository.findByIdOptional(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));

        if (!group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        return activityRepository.findByGroupId(groupId).stream()
                .filter(a -> a instanceof Trip)
                .map(a -> tripMapper.toDto((Trip) a))
                .collect(Collectors.toList());
    }
}
