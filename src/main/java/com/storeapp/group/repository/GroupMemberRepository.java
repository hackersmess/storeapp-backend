package com.storeapp.group.repository;

import com.storeapp.group.entity.GroupMember;
import com.storeapp.group.entity.GroupRole;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

/**
 * Repository per i membri dei gruppi
 */
@ApplicationScoped
public class GroupMemberRepository implements PanacheRepository<GroupMember> {

    /**
     * Trova un membro specifico in un gruppo
     */
    public Optional<GroupMember> findByGroupAndUser(Long groupId, Long userId) {
        return find("group.id = ?1 and user.id = ?2", groupId, userId).firstResultOptional();
    }

    /**
     * Trova tutti i membri di un gruppo
     */
    public List<GroupMember> findByGroup(Long groupId) {
        return find("group.id", groupId).list();
    }

    /**
     * Trova tutti gli admin di un gruppo
     */
    public List<GroupMember> findAdminsByGroup(Long groupId) {
        return find("group.id = ?1 and role = ?2", groupId, GroupRole.ADMIN).list();
    }

    /**
     * Conta quanti admin ha un gruppo
     */
    public long countAdminsByGroup(Long groupId) {
        return count("group.id = ?1 and role = ?2", groupId, GroupRole.ADMIN);
    }

    /**
     * Conta i membri di un gruppo
     */
    public long countByGroup(Long groupId) {
        return count("group.id", groupId);
    }

    /**
     * Verifica se un utente è membro di un gruppo
     */
    public boolean isMember(Long groupId, Long userId) {
        return count("group.id = ?1 and user.id = ?2", groupId, userId) > 0;
    }

    /**
     * Verifica se un utente è admin di un gruppo
     */
    public boolean isAdmin(Long groupId, Long userId) {
        return count("group.id = ?1 and user.id = ?2 and role = ?3", 
                    groupId, userId, GroupRole.ADMIN) > 0;
    }

    /**
     * Rimuove un membro tramite query DELETE
     */
    public long removeMemberById(Long memberId) {
        return delete("id", memberId);
    }
}
