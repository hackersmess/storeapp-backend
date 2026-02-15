package com.storeapp.activity.service;

import com.storeapp.group.entity.Group;
import com.storeapp.group.entity.GroupMember;
import com.storeapp.group.repository.GroupRepository;
import com.storeapp.group.repository.GroupMemberRepository;
import com.storeapp.activity.dto.*;
import com.storeapp.activity.entity.*;
import com.storeapp.activity.exception.ActivityNotFoundException;
import com.storeapp.activity.exception.InvalidExpenseSplitException;
import com.storeapp.activity.mapper.*;
import com.storeapp.activity.repository.ActivityRepository;
import com.storeapp.activity.repository.ActivityParticipantRepository;
import com.storeapp.activity.repository.ActivityExpenseRepository;
import com.storeapp.activity.repository.ActivityExpenseSplitRepository;
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
    GroupRepository groupRepository;

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
    public ActivityDto createActivity(Long groupId, ActivityRequest request, Long userId) {
        Group group = groupRepository.findByIdWithMembers(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));

        if (!group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        Activity activity = activityMapper.toEntity(request);
        activity.group = group;
        activityRepository.persist(activity);

        return activityMapper.toDto(activity);
    }

    public ActivityDto getActivity(Long activityId, Long userId) {
        Activity activity = activityRepository.findByIdOptional(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        return activityMapper.toDto(activity);
    }

    public ActivityDto getActivityWithDetails(Long activityId, Long userId) {
        Activity activity = activityRepository.findByIdOptional(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        ActivityDto dto = activityMapper.toDto(activity);
        List<ActivityParticipant> participants = participantRepository.findByActivityId(activityId);
        dto.setParticipants(participantMapper.toDtoList(participants));
        List<ActivityExpense> expenses = expenseRepository.findByActivityId(activityId);
        dto.setExpenses(expenseMapper.toDtoList(expenses));

        return dto;
    }

    public List<ActivityDto> getActivitiesByGroup(Long groupId, Long userId) {
        Group group = groupRepository.findByIdWithMembers(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));

        if (!group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        List<Activity> activities = activityRepository.findByGroupId(groupId);
        return activityMapper.toDtoList(activities);
    }

    @Transactional
    public ActivityDto updateActivity(Long activityId, ActivityRequest request, Long userId) {
        Activity activity = activityRepository.findByIdOptional(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.group.isMember(userId)) {
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

        if (!activity.group.isAdmin(userId)) {
            throw new RuntimeException("Only group admins can delete activities");
        }

        activityRepository.delete(activity);
    }

    @Transactional
    public ActivityDto toggleActivityCompletion(Long activityId, Long userId) {
        Activity activity = activityRepository.findByIdOptional(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        activity.isCompleted = !activity.isCompleted;
        return activityMapper.toDto(activity);
    }

    @Transactional
    public void reorderActivities(Long groupId, List<Long> activityIds, Long userId) {
        Group group = groupRepository.findByIdWithMembers(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));

        if (!group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        for (int i = 0; i < activityIds.size(); i++) {
            Long activityId = activityIds.get(i);
            Activity activity = activityRepository.findByIdOptional(activityId)
                    .orElseThrow(() -> new ActivityNotFoundException(activityId));

            if (!activity.group.id.equals(groupId)) {
                throw new RuntimeException("Activity does not belong to this group");
            }

            activity.displayOrder = i;
        }
    }

    @Transactional
    public ActivityParticipantDto addParticipant(Long activityId, ActivityParticipantRequest request, Long userId) {
        Activity activity = activityRepository.findByIdOptional(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        GroupMember member = groupMemberRepository.findByIdOptional(request.groupMemberId)
                .orElseThrow(() -> new RuntimeException("Group member not found"));

        if (!member.group.id.equals(activity.group.id)) {
            throw new RuntimeException("Member does not belong to this group");
        }

        ActivityParticipant participant = new ActivityParticipant();
        participant.activity = activity;
        participant.groupMember = member;
        participant.status = request.status != null ? request.status : ParticipantStatus.CONFIRMED;
        participant.notes = request.notes;

        participantRepository.persist(participant);
        return participantMapper.toDto(participant);
    }

    @Transactional
    public ActivityParticipantDto updateParticipantStatus(Long participantId, ParticipantStatus status, String notes, Long userId) {
        ActivityParticipant participant = participantRepository.findByIdOptional(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        if (!participant.activity.group.isMember(userId)) {
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

        if (!participant.activity.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        participantRepository.delete(participant);
    }

    public List<ActivityParticipantDto> getParticipantsByActivity(Long activityId, Long userId) {
        Activity activity = activityRepository.findByIdOptional(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        List<ActivityParticipant> participants = participantRepository.findByActivityId(activityId);
        return participantMapper.toDtoList(participants);
    }

    @Transactional
    public ActivityExpenseDto addExpense(Long activityId, ActivityExpenseRequest request, Long userId) {
        Activity activity = activityRepository.findByIdOptional(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        GroupMember payer = groupMemberRepository.findByIdOptional(request.paidByGroupMemberId)
                .orElseThrow(() -> new RuntimeException("Payer not found"));

        if (!payer.group.id.equals(activity.group.id)) {
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

        if (!activity.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        List<ActivityExpense> expenses = expenseRepository.findByActivityId(activityId);
        return expenseMapper.toDtoList(expenses);
    }

    @Transactional
    public void deleteExpense(Long expenseId, Long userId) {
        ActivityExpense expense = expenseRepository.findByIdOptional(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.activity.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        expenseRepository.delete(expense);
    }
}