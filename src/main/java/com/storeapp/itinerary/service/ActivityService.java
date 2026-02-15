package com.storeapp.itinerary.service;

import com.storeapp.group.entity.GroupMember;
import com.storeapp.group.repository.GroupMemberRepository;
import com.storeapp.itinerary.dto.*;
import com.storeapp.itinerary.entity.*;
import com.storeapp.itinerary.exception.*;
import com.storeapp.itinerary.mapper.*;
import com.storeapp.itinerary.repository.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class ActivityService {

    @Inject
    ActivityRepository activityRepository;

    @Inject
    ItineraryRepository itineraryRepository;

    @Inject
    ActivityParticipantRepository participantRepository;

    @Inject
    ActivityExpenseRepository expenseRepository;

    @Inject
    ActivityExpenseSplitRepository expenseSplitRepository;

    @Inject
    GroupMemberRepository groupMemberRepository;

    @Inject
    ActivityMapper activityMapper;

    @Inject
    ActivityParticipantMapper participantMapper;

    @Inject
    ActivityExpenseMapper expenseMapper;

    @Inject
    ActivityExpenseSplitMapper expenseSplitMapper;

    @Transactional
    public ActivityDto createActivity(Long itineraryId, ActivityRequest request, Long userId) {
        Itinerary itinerary = itineraryRepository.findByIdOptional(itineraryId)
                .orElseThrow(() -> new ItineraryNotFoundException(itineraryId));

        if (!itinerary.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        Activity activity = activityMapper.toEntity(request);
        activity.itinerary = itinerary;
        activityRepository.persist(activity);

        return activityMapper.toDto(activity);
    }

    public ActivityDto getActivity(Long activityId, Long userId) {
        Activity activity = activityRepository.findByIdOptional(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.itinerary.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        return activityMapper.toDto(activity);
    }

    public ActivityDto getActivityWithDetails(Long activityId, Long userId) {
        Activity activity = activityRepository.findByIdOptional(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.itinerary.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        ActivityDto dto = activityMapper.toDto(activity);
        List<ActivityParticipant> participants = participantRepository.findByActivityId(activityId);
        dto.participants = participantMapper.toDtoList(participants);
        List<ActivityExpense> expenses = expenseRepository.findByActivityId(activityId);
        dto.expenses = expenseMapper.toDtoList(expenses);

        return dto;
    }

    public List<ActivityDto> getActivitiesByItinerary(Long itineraryId, Long userId) {
        Itinerary itinerary = itineraryRepository.findByIdOptional(itineraryId)
                .orElseThrow(() -> new ItineraryNotFoundException(itineraryId));

        if (!itinerary.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        List<Activity> activities = activityRepository.findByItineraryId(itineraryId);
        return activityMapper.toDtoList(activities);
    }

    @Transactional
    public ActivityDto updateActivity(Long activityId, ActivityRequest request, Long userId) {
        Activity activity = activityRepository.findByIdOptional(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.itinerary.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        activity.name = request.name;
        activity.description = request.description;
        activity.scheduledDate = request.scheduledDate;
        activity.startTime = request.startTime;
        activity.endTime = request.endTime;
        activity.locationName = request.locationName;
        activity.locationAddress = request.locationAddress;
        activity.locationLat = request.locationLat;
        activity.locationLng = request.locationLng;
        activity.locationProvider = request.locationProvider;
        activity.locationMetadata = request.locationMetadata;

        return activityMapper.toDto(activity);
    }

    @Transactional
    public void deleteActivity(Long activityId, Long userId) {
        Activity activity = activityRepository.findByIdOptional(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.itinerary.group.isAdmin(userId)) {
            throw new RuntimeException("Only group admins can delete activities");
        }

        activityRepository.delete(activity);
    }

    @Transactional
    public ActivityDto toggleActivityCompletion(Long activityId, Long userId) {
        Activity activity = activityRepository.findByIdOptional(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.itinerary.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        activity.isCompleted = !activity.isCompleted;
        return activityMapper.toDto(activity);
    }

    @Transactional
    public void reorderActivities(Long itineraryId, List<Long> activityIds, Long userId) {
        Itinerary itinerary = itineraryRepository.findByIdOptional(itineraryId)
                .orElseThrow(() -> new ItineraryNotFoundException(itineraryId));

        if (!itinerary.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        for (int i = 0; i < activityIds.size(); i++) {
            Long activityId = activityIds.get(i);
            Activity activity = activityRepository.findByIdOptional(activityId)
                    .orElseThrow(() -> new ActivityNotFoundException(activityId));

            if (!activity.itinerary.id.equals(itineraryId)) {
                throw new RuntimeException("Activity does not belong to this itinerary");
            }

            activity.displayOrder = i;
        }
    }

    @Transactional
    public ActivityParticipantDto addParticipant(Long activityId, ActivityParticipantRequest request, Long userId) {
        Activity activity = activityRepository.findByIdOptional(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.itinerary.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        GroupMember groupMember = groupMemberRepository.findByIdOptional(request.groupMemberId)
                .orElseThrow(() -> new RuntimeException("Group member not found"));

        if (!groupMember.group.id.equals(activity.itinerary.group.id)) {
            throw new RuntimeException("Member does not belong to this group");
        }

        ActivityParticipant participant = new ActivityParticipant();
        participant.activity = activity;
        participant.groupMember = groupMember;
        participant.status = request.status != null ? request.status : ParticipantStatus.CONFIRMED;
        participant.notes = request.notes;

        participantRepository.persist(participant);
        return participantMapper.toDto(participant);
    }

    @Transactional
    public ActivityParticipantDto updateParticipantStatus(Long participantId, ParticipantStatus status, String notes, Long userId) {
        ActivityParticipant participant = participantRepository.findByIdOptional(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        if (!participant.activity.itinerary.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        participant.status = status;
        if (notes != null) {
            participant.notes = notes;
        }

        return participantMapper.toDto(participant);
    }

    @Transactional
    public void removeParticipant(Long participantId, Long userId) {
        ActivityParticipant participant = participantRepository.findByIdOptional(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        if (!participant.activity.itinerary.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        participantRepository.delete(participant);
    }

    public List<ActivityParticipantDto> getParticipantsByActivity(Long activityId, Long userId) {
        Activity activity = activityRepository.findByIdOptional(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.itinerary.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        List<ActivityParticipant> participants = participantRepository.findByActivityId(activityId);
        return participantMapper.toDtoList(participants);
    }

    @Transactional
    public ActivityExpenseDto addExpense(Long activityId, ActivityExpenseRequest request, Long userId) {
        Activity activity = activityRepository.findByIdOptional(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.itinerary.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        GroupMember payer = groupMemberRepository.findByIdOptional(request.paidByGroupMemberId)
                .orElseThrow(() -> new RuntimeException("Payer not found"));

        if (!payer.group.id.equals(activity.itinerary.group.id)) {
            throw new RuntimeException("Payer is not a member of this group");
        }

        ActivityExpense expense = new ActivityExpense();
        expense.activity = activity;
        expense.description = request.description;
        expense.amount = request.amount;
        expense.paidBy = payer;

        expenseRepository.persist(expense);

        if (request.splits != null && !request.splits.isEmpty()) {
            BigDecimal totalSplits = request.splits.stream()
                    .map(split -> split.amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalSplits.compareTo(request.amount) != 0) {
                throw new InvalidExpenseSplitException("Splits must sum to total expense amount");
            }

            for (ActivityExpenseRequest.ExpenseSplitRequest splitRequest : request.splits) {
                GroupMember member = groupMemberRepository.findByIdOptional(splitRequest.groupMemberId)
                        .orElseThrow(() -> new RuntimeException("Member not found in split"));

                ActivityExpenseSplit split = new ActivityExpenseSplit();
                split.expense = expense;
                split.groupMember = member;
                split.amount = splitRequest.amount;
                split.isSettled = splitRequest.isSettled;

                expenseSplitRepository.persist(split);
            }
        }

        ActivityExpense savedExpense = expenseRepository.findByIdOptional(expense.id)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve saved expense"));

        return expenseMapper.toDto(savedExpense);
    }

    public List<ActivityExpenseDto> getExpensesByActivity(Long activityId, Long userId) {
        Activity activity = activityRepository.findByIdOptional(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.itinerary.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        List<ActivityExpense> expenses = expenseRepository.findByActivityId(activityId);
        return expenseMapper.toDtoList(expenses);
    }

    @Transactional
    public void deleteExpense(Long expenseId, Long userId) {
        ActivityExpense expense = expenseRepository.findByIdOptional(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.activity.itinerary.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        expenseRepository.delete(expense);
    }
}