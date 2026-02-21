package com.storeapp.group.controller;

import com.storeapp.group.dto.*;
import com.storeapp.group.service.GroupService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

/**
 * Controller REST per la gestione dei gruppi
 */
@Path("/api/groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class GroupController {

    @Inject
    GroupService groupService;

    @Inject
    JsonWebToken jwt;

    /**
     * Ottiene l'ID dell'utente corrente dal token JWT
     */
    private Long getCurrentUserId() {
        return Long.parseLong(jwt.getSubject());
    }

    /**
     * Crea un nuovo gruppo
     * POST /api/groups
     */
    @POST
    public GroupDto createGroup(@Valid CreateGroupRequest request) {
        return groupService.createGroup(request, getCurrentUserId());
    }

    /**
     * Ottiene tutti i gruppi dell'utente corrente
     * GET /api/groups
     */
    @GET
    public List<GroupDto> getMyGroups() {
        return groupService.getUserGroups(getCurrentUserId());
    }

    /**
     * Ottiene i dettagli di un gruppo specifico
     * GET /api/groups/{id}
     */
    @GET
    @Path("/{id}")
    public GroupDto getGroup(@PathParam("id") Long id) {
        return groupService.getGroupDetails(id, getCurrentUserId());
    }

    /**
     * Aggiorna un gruppo
     * PUT /api/groups/{id}
     */
    @PUT
    @Path("/{id}")
    public GroupDto updateGroup(@PathParam("id") Long id, @Valid UpdateGroupRequest request) {
        return groupService.updateGroup(id, request, getCurrentUserId());
    }

    /**
     * Elimina un gruppo
     * DELETE /api/groups/{id}
     */
    @DELETE
    @Path("/{id}")
    public void deleteGroup(@PathParam("id") Long id) {
        groupService.deleteGroup(id, getCurrentUserId());
    }

    /**
     * Ottiene tutti i membri di un gruppo
     * GET /api/groups/{id}/members
     */
    @GET
    @Path("/{id}/members")
    public List<GroupMemberDto> getGroupMembers(@PathParam("id") Long id) {
        return groupService.getGroupMembers(id, getCurrentUserId());
    }

    /**
     * Ottiene la lista di utenti disponibili da aggiungere al gruppo
     * (esclude gli utenti già membri)
     * GET /api/groups/{id}/available-users?search={query}
     * 
     * @param groupId ID del gruppo
     * @param search Query di ricerca opzionale (cerca in email e nome)
     */
    @GET
    @Path("/{id}/available-users")
    public List<com.storeapp.user.dto.UserResponse> getAvailableUsers(
            @PathParam("id") Long groupId,
            @QueryParam("search") String search) {
        return groupService.getAvailableUsers(groupId, getCurrentUserId(), search);
    }

    /**
     * Aggiunge un membro al gruppo
     * POST /api/groups/{id}/members
     */
    @POST
    @Path("/{id}/members")
    public GroupMemberDto addMember(@PathParam("id") Long id, @Valid AddMemberRequest request) {
        return groupService.addMember(id, request, getCurrentUserId());
    }

    /**
     * Aggiunge più membri al gruppo in una singola transazione atomica
     * POST /api/groups/{id}/members/batch
     * Se anche solo uno fallisce, nessun membro viene aggiunto (rollback)
     */
    @POST
    @Path("/{id}/members/batch")
    public List<GroupMemberDto> addMembers(@PathParam("id") Long id, @Valid List<AddMemberRequest> requests) {
        return groupService.addMembers(id, requests, getCurrentUserId());
    }

    /**
     * Rimuove un membro dal gruppo
     * DELETE /api/groups/{groupId}/members/{memberId}
     */
    @DELETE
    @Path("/{groupId}/members/{memberId}")
    public void removeMember(@PathParam("groupId") Long groupId, @PathParam("memberId") Long memberId) {
        groupService.removeMember(groupId, memberId, getCurrentUserId());
    }

    /**
     * Verifica lo stato prima di abbandonare un gruppo
     * GET /api/groups/{id}/leave/status
     */
    @GET
    @Path("/{id}/leave/status")
    public LeaveGroupStatusDto checkLeaveGroupStatus(@PathParam("id") Long id) {
        return groupService.checkLeaveGroupStatus(id, getCurrentUserId());
    }

    /**
     * Abbandona un gruppo
     * POST /api/groups/{id}/leave
     */
    @POST
    @Path("/{id}/leave")
    public void leaveGroup(@PathParam("id") Long id) {
        groupService.leaveGroup(id, getCurrentUserId());
    }

    /**
     * Cambia il ruolo di un membro
     * PUT /api/groups/{groupId}/members/{memberId}/role
     */
    @PUT
    @Path("/{groupId}/members/{memberId}/role")
    public GroupMemberDto updateMemberRole(
            @PathParam("groupId") Long groupId,
            @PathParam("memberId") Long memberId,
            @Valid UpdateMemberRoleRequest request) {
        return groupService.updateMemberRole(groupId, memberId, request, getCurrentUserId());
    }
}
