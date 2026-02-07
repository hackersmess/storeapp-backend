package com.storeapp.controller;

import com.storeapp.dto.CreateUserRequest;
import com.storeapp.dto.UpdateUserRequest;
import com.storeapp.dto.UserResponse;
import com.storeapp.service.UserBusinessService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * REST Controller per gli endpoint degli utenti.
 * Espone solo le API REST, delega tutta la logica al Business Service.
 */
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserRestController {

    @Inject
    UserBusinessService userBusinessService;

    /**
     * GET /api/users
     * Lista tutti gli utenti.
     */
    @GET
    public List<UserResponse> listAll() {
        return userBusinessService.findAll();
    }

    /**
     * GET /api/users/{id}
     * Recupera un utente per ID.
     */
    @GET
    @Path("/{id}")
    public UserResponse getById(@PathParam("id") Long id) {
        return userBusinessService.findById(id);
    }

    /**
     * GET /api/users/email/{email}
     * Recupera un utente per email.
     */
    @GET
    @Path("/email/{email}")
    public UserResponse getByEmail(@PathParam("email") String email) {
        return userBusinessService.findByEmail(email);
    }

    /**
     * POST /api/users
     * Crea un nuovo utente.
     */
    @POST
    public Response create(@Valid CreateUserRequest request) {
        UserResponse user = userBusinessService.create(request);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    /**
     * PUT /api/users/{id}
     * Aggiorna un utente esistente.
     */
    @PUT
    @Path("/{id}")
    public UserResponse update(@PathParam("id") Long id, @Valid UpdateUserRequest request) {
        return userBusinessService.update(id, request);
    }

    /**
     * DELETE /api/users/{id}
     * Elimina un utente.
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        userBusinessService.delete(id);
        return Response.noContent().build();
    }

    /**
     * GET /api/users/count
     * Conta il numero totale di utenti.
     */
    @GET
    @Path("/count")
    public long count() {
        return userBusinessService.count();
    }
}

