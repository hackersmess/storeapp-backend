package com.storeapp.group.service;

import com.storeapp.group.dto.*;
import com.storeapp.group.entity.Group;
import com.storeapp.group.entity.GroupMember;
import com.storeapp.group.entity.GroupRole;
import com.storeapp.group.exception.*;
import com.storeapp.group.mapper.GroupMapper;
import com.storeapp.group.mapper.GroupMemberMapper;
import com.storeapp.group.repository.GroupMemberRepository;
import com.storeapp.group.repository.GroupRepository;
import com.storeapp.user.entity.User;
import com.storeapp.user.mapper.UserMapper;
import com.storeapp.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

/**
 * Service per la gestione dei gruppi
 * Le conversioni Entity <-> DTO sono delegate ai Mapper
 */
@ApplicationScoped
public class GroupService {

    @Inject
    GroupRepository groupRepository;

    @Inject
    GroupMemberRepository groupMemberRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    GroupMapper groupMapper;

    @Inject
    GroupMemberMapper groupMemberMapper;

    @Inject
    UserMapper userMapper;

    private static final int MAX_MEMBERS_PER_GROUP = 50;

    /**
     * Crea un nuovo gruppo
     * Il creatore diventa automaticamente ADMIN del gruppo
     */
    @Transactional
    public GroupDto createGroup(CreateGroupRequest request, Long userId) {
        User creator = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        // Usa mapper per creare entity da request
        Group group = groupMapper.toEntity(request);
        group.createdBy = creator;

        // Persiste il gruppo
        groupRepository.persist(group);
        groupRepository.flush(); // Forza il flush per ottenere l'ID

        // Aggiungi esplicitamente il creatore come ADMIN
        GroupMember creatorMember = new GroupMember();
        creatorMember.group = group;
        creatorMember.user = creator;
        creatorMember.role = GroupRole.ADMIN;
        groupMemberRepository.persist(creatorMember);
        groupMemberRepository.flush();

        // Ricarica il gruppo con i membri per avere i dati completi
        group = groupRepository.findByIdWithMembers(group.id)
            .orElseThrow(() -> new RuntimeException("Errore nel recupero del gruppo creato"));

        return groupMapper.toDtoWithMembers(group);
    }

    /**
     * Ottiene tutti i gruppi di un utente (creati o come membro)
     */
    public List<GroupDto> getUserGroups(Long userId) {
        List<Group> groups = groupRepository.findByMember(userId);
        return groupMapper.toDtoList(groups);
    }

    /**
     * Ottiene i dettagli di un gruppo con i membri
     */
    public GroupDto getGroupDetails(Long groupId, Long userId) {
        Group group = groupRepository.findByIdWithMembers(groupId)
            .orElseThrow(() -> new GroupNotFoundException(groupId));

        // Verifica che l'utente sia membro del gruppo
        if (!group.isMember(userId)) {
            throw InsufficientPermissionsException.adminRequired();
        }

        return groupMapper.toDtoWithMembers(group);
    }

    /**
     * Aggiorna le informazioni di un gruppo (solo ADMIN)
     */
    @Transactional
    public GroupDto updateGroup(Long groupId, UpdateGroupRequest request, Long userId) {
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            throw new GroupNotFoundException(groupId);
        }

        // Solo gli admin possono modificare
        if (!group.isAdmin(userId)) {
            throw InsufficientPermissionsException.adminRequired();
        }

        // Usa mapper per aggiornare entity (partial update)
        groupMapper.updateEntityFromRequest(request, group);

        groupRepository.persist(group);
        return groupMapper.toDtoWithMembers(group);
    }

    /**
     * Elimina un gruppo (solo creatore)
     */
    @Transactional
    public void deleteGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            throw new GroupNotFoundException(groupId);
        }

        // Solo il creatore può eliminare
        if (!group.isCreator(userId)) {
            throw InsufficientPermissionsException.creatorRequired();
        }

        groupRepository.delete(group);
    }

    /**
     * Ottiene la lista di utenti disponibili da aggiungere al gruppo
     * Esclude gli utenti già membri del gruppo
     */
    public List<com.storeapp.user.dto.UserResponse> getAvailableUsers(Long groupId, Long userId) {
        Group group = groupRepository.findByIdWithMembers(groupId)
            .orElseThrow(() -> new GroupNotFoundException(groupId));

        // Verifica che l'utente sia almeno membro del gruppo
        if (!group.isMember(userId)) {
            throw InsufficientPermissionsException.memberRequired();
        }

        // Ottieni tutti gli utenti
        List<User> allUsers = userRepository.findAll();

        // Ottieni gli ID dei membri attuali del gruppo
        List<Long> memberIds = group.members.stream()
            .map(m -> m.user.getId())
            .toList();

        // Filtra gli utenti che NON sono già membri
        return allUsers.stream()
            .filter(user -> !memberIds.contains(user.getId()))
            .map(userMapper::toUserResponse)
            .toList();
    }

    /**
     * Aggiunge un membro al gruppo (solo ADMIN)
     */
    @Transactional
    public GroupMemberDto addMember(Long groupId, AddMemberRequest request, Long userId) {
        Group group = groupRepository.findByIdWithMembers(groupId)
            .orElseThrow(() -> new GroupNotFoundException(groupId));

        // Solo gli admin possono aggiungere membri
        if (!group.isAdmin(userId)) {
            throw InsufficientPermissionsException.adminRequired();
        }

        // Verifica limite membri
        if (group.getMemberCount() >= MAX_MEMBERS_PER_GROUP) {
            throw InvalidOperationException.maxMembersReached();
        }

        // Trova l'utente da aggiungere
        User userToAdd = null;
        if (request.email != null && !request.email.isBlank()) {
            userToAdd = userRepository.findByEmail(request.email).orElse(null);
        } else if (request.username != null && !request.username.isBlank()) {
            userToAdd = userRepository.findByName(request.username).orElse(null);
        }

        if (userToAdd == null) {
            throw new NotFoundException("Utente non trovato");
        }

        // Verifica che non sia già membro
        if (groupMemberRepository.isMember(groupId, userToAdd.getId())) {
            throw new UserAlreadyMemberException(userToAdd.getEmail());
        }

        // Aggiungi il membro
        GroupMember member = new GroupMember();
        member.group = group;
        member.user = userToAdd;
        member.role = request.role != null ? request.role : GroupRole.MEMBER;
        groupMemberRepository.persist(member);

        return groupMemberMapper.toDto(member);
    }

    /**
     * Rimuove un membro dal gruppo (solo ADMIN)
     */
    @Transactional
    public void removeMember(Long groupId, Long memberId, Long userId) {
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            throw new GroupNotFoundException(groupId);
        }

        // Solo gli admin possono rimuovere membri
        if (!group.isAdmin(userId)) {
            throw InsufficientPermissionsException.adminRequired();
        }

        GroupMember memberToRemove = groupMemberRepository.findById(memberId);
        if (memberToRemove == null || !memberToRemove.group.id.equals(groupId)) {
            throw new NotFoundException("Membro non trovato in questo gruppo");
        }

        // Non puoi rimuovere te stesso (usa leaveGroup)
        if (memberToRemove.user.getId().equals(userId)) {
            throw InvalidOperationException.cannotRemoveYourself();
        }

        // Se è un admin, verifica che non sia l'ultimo
        if (memberToRemove.role == GroupRole.ADMIN) {
            long adminCount = groupMemberRepository.countAdminsByGroup(groupId);
            if (adminCount <= 1) {
                throw InvalidOperationException.lastAdminCannotLeave();
            }
        }

        groupMemberRepository.delete(memberToRemove);
    }

    /**
     * Abbandona un gruppo (self-remove)
     */
    @Transactional
    public void leaveGroup(Long groupId, Long userId) {
        GroupMember membership = groupMemberRepository.findByGroupAndUser(groupId, userId)
            .orElseThrow(() -> new NotFoundException("Non sei membro di questo gruppo"));

        // Se sei admin, verifica che non sei l'ultimo
        if (membership.role == GroupRole.ADMIN) {
            long adminCount = groupMemberRepository.countAdminsByGroup(groupId);
            if (adminCount <= 1) {
                throw InvalidOperationException.lastAdminCannotLeave();
            }
        }

        groupMemberRepository.delete(membership);
    }

    /**
     * Cambia il ruolo di un membro (solo ADMIN)
     */
    @Transactional
    public GroupMemberDto updateMemberRole(Long groupId, Long memberId, UpdateMemberRoleRequest request, Long userId) {
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            throw new GroupNotFoundException(groupId);
        }

        // Solo gli admin possono cambiare ruoli
        if (!group.isAdmin(userId)) {
            throw InsufficientPermissionsException.adminRequired();
        }

        GroupMember member = groupMemberRepository.findById(memberId);
        if (member == null || !member.group.id.equals(groupId)) {
            throw new NotFoundException("Membro non trovato in questo gruppo");
        }

        // Se stai retrocedendo un admin, verifica che non sia l'ultimo
        if (member.role == GroupRole.ADMIN && request.role == GroupRole.MEMBER) {
            long adminCount = groupMemberRepository.countAdminsByGroup(groupId);
            if (adminCount <= 1) {
                throw InvalidOperationException.lastAdminCannotLeave();
            }
        }

        member.role = request.role;
        groupMemberRepository.persist(member);

        return groupMemberMapper.toDto(member);
    }

    /**
     * Ottiene tutti i membri di un gruppo
     */
    public List<GroupMemberDto> getGroupMembers(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            throw new GroupNotFoundException(groupId);
        }

        // Verifica che l'utente sia membro
        if (!group.isMember(userId)) {
            throw InsufficientPermissionsException.adminRequired();
        }

        List<GroupMember> members = groupMemberRepository.findByGroup(groupId);
        return groupMemberMapper.toDtoList(members);
    }
}
