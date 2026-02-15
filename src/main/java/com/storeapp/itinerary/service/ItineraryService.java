package com.storeapp.itinerary.service;

import com.storeapp.group.entity.Group;
import com.storeapp.group.exception.GroupNotFoundException;
import com.storeapp.group.exception.InsufficientPermissionsException;
import com.storeapp.group.repository.GroupRepository;
import com.storeapp.itinerary.dto.ItineraryDto;
import com.storeapp.itinerary.dto.ItineraryRequest;
import com.storeapp.itinerary.entity.Itinerary;
import com.storeapp.itinerary.exception.ItineraryAlreadyExistsException;
import com.storeapp.itinerary.exception.ItineraryNotFoundException;
import com.storeapp.itinerary.mapper.ItineraryMapper;
import com.storeapp.itinerary.repository.ItineraryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Service per la gestione degli itinerari
 */
@ApplicationScoped
public class ItineraryService {

    @Inject
    ItineraryRepository itineraryRepository;

    @Inject
    GroupRepository groupRepository;

    @Inject
    ItineraryMapper itineraryMapper;

    /**
     * Crea un nuovo itinerario per un gruppo
     * Solo i membri del gruppo possono creare l'itinerario
     * Un gruppo può avere un solo itinerario (relazione 1:1)
     */
    @Transactional
    public ItineraryDto createItinerary(Long groupId, ItineraryRequest request, Long userId) {
        // Verifica che il gruppo esista
        Group group = groupRepository.findByIdOptional(groupId)
            .orElseThrow(() -> new GroupNotFoundException(groupId));

        // Verifica che l'utente sia membro del gruppo
        if (!group.isMember(userId)) {
            throw new InsufficientPermissionsException("Devi essere membro del gruppo per creare un itinerario");
        }

        // Verifica che il gruppo non abbia già un itinerario
        if (itineraryRepository.existsByGroupId(groupId)) {
            throw new ItineraryAlreadyExistsException(groupId);
        }

        // Crea l'itinerario
        Itinerary itinerary = itineraryMapper.toEntity(request);
        itinerary.group = group;

        itineraryRepository.persist(itinerary);

        return itineraryMapper.toDto(itinerary);
    }

    /**
     * Ottiene l'itinerario di un gruppo
     */
    @Transactional
    public ItineraryDto getItinerary(Long groupId, Long userId) {
        // Verifica che il gruppo esista
        Group group = groupRepository.findByIdOptional(groupId)
            .orElseThrow(() -> new GroupNotFoundException(groupId));

        // Verifica che l'utente sia membro del gruppo
        if (!group.isMember(userId)) {
            throw new InsufficientPermissionsException("Devi essere membro del gruppo per visualizzare l'itinerario");
        }

        // Trova l'itinerario
        Itinerary itinerary = itineraryRepository.findByGroupId(groupId)
            .orElseThrow(() -> new ItineraryNotFoundException("L'itinerario per questo gruppo non esiste ancora"));

        return itineraryMapper.toDto(itinerary);
    }

    /**
     * Ottiene uno specifico itinerario per ID
     */
    @Transactional
    public ItineraryDto getItineraryById(Long groupId, Long itineraryId, Long userId) {
        // Verifica che il gruppo esista
        Group group = groupRepository.findByIdOptional(groupId)
            .orElseThrow(() -> new GroupNotFoundException(groupId));

        // Verifica che l'utente sia membro del gruppo
        if (!group.isMember(userId)) {
            throw new InsufficientPermissionsException("Devi essere membro del gruppo per visualizzare l'itinerario");
        }

        // Trova l'itinerario
        Itinerary itinerary = itineraryRepository.findByIdOptional(itineraryId)
            .orElseThrow(() -> new ItineraryNotFoundException(itineraryId));

        // Verifica che l'itinerario appartenga al gruppo
        if (!itinerary.group.id.equals(groupId)) {
            throw new ItineraryNotFoundException("L'itinerario non appartiene a questo gruppo");
        }

        return itineraryMapper.toDto(itinerary);
    }

    /**
     * Ottiene l'itinerario con tutte le attività
     */
    @Transactional
    public ItineraryDto getItineraryWithActivities(Long groupId, Long userId) {
        // Verifica che il gruppo esista
        Group group = groupRepository.findByIdOptional(groupId)
            .orElseThrow(() -> new GroupNotFoundException(groupId));

        // Verifica che l'utente sia membro del gruppo
        if (!group.isMember(userId)) {
            throw new InsufficientPermissionsException("Devi essere membro del gruppo per visualizzare l'itinerario");
        }

        // Trova l'itinerario
        Itinerary itinerary = itineraryRepository.findByGroupId(groupId)
            .orElseThrow(() -> new ItineraryNotFoundException("L'itinerario per questo gruppo non esiste ancora"));

        return itineraryMapper.toDtoWithActivities(itinerary);
    }

    /**
     * Aggiorna un itinerario
     * Solo i membri del gruppo possono aggiornare l'itinerario
     */
    @Transactional
    public ItineraryDto updateItinerary(Long itineraryId, ItineraryRequest request, Long userId) {
        // Trova l'itinerario
        Itinerary itinerary = itineraryRepository.findByIdOptional(itineraryId)
            .orElseThrow(() -> new ItineraryNotFoundException(itineraryId));

        // Verifica che l'utente sia membro del gruppo
        if (!itinerary.group.isMember(userId)) {
            throw new InsufficientPermissionsException("Devi essere membro del gruppo per modificare l'itinerario");
        }

        // Aggiorna l'itinerario
        itineraryMapper.updateEntityFromRequest(request, itinerary);

        return itineraryMapper.toDto(itinerary);
    }

    /**
     * Elimina un itinerario
     * Solo gli admin del gruppo possono eliminare l'itinerario
     */
    @Transactional
    public void deleteItinerary(Long itineraryId, Long userId) {
        // Trova l'itinerario
        Itinerary itinerary = itineraryRepository.findByIdOptional(itineraryId)
            .orElseThrow(() -> new ItineraryNotFoundException(itineraryId));

        // Verifica che l'utente sia admin del gruppo
        if (!itinerary.group.isAdmin(userId)) {
            throw new InsufficientPermissionsException("Solo gli admin possono eliminare l'itinerario");
        }

        // Elimina l'itinerario (CASCADE elimina automaticamente le attività)
        itineraryRepository.delete(itinerary);
    }

    /**
     * Ottiene uno specifico itinerario con tutte le attività
     */
    @Transactional
    public ItineraryDto getItineraryByIdWithActivities(Long groupId, Long itineraryId, Long userId) {
        // Verifica che il gruppo esista
        Group group = groupRepository.findByIdOptional(groupId)
            .orElseThrow(() -> new GroupNotFoundException(groupId));

        // Verifica che l'utente sia membro del gruppo
        if (!group.isMember(userId)) {
            throw new InsufficientPermissionsException("Devi essere membro del gruppo per visualizzare l'itinerario");
        }

        // Trova l'itinerario
        Itinerary itinerary = itineraryRepository.findByIdOptional(itineraryId)
            .orElseThrow(() -> new ItineraryNotFoundException(itineraryId));

        // Verifica che l'itinerario appartenga al gruppo
        if (!itinerary.group.id.equals(groupId)) {
            throw new ItineraryNotFoundException("L'itinerario non appartiene a questo gruppo");
        }

        return itineraryMapper.toDtoWithActivities(itinerary);
    }

    /**
     * Aggiorna uno specifico itinerario per ID
     */
    @Transactional
    public ItineraryDto updateItineraryById(Long groupId, Long itineraryId, ItineraryRequest request, Long userId) {
        // Verifica che il gruppo esista
        Group group = groupRepository.findByIdOptional(groupId)
            .orElseThrow(() -> new GroupNotFoundException(groupId));

        // Verifica che l'utente sia membro del gruppo
        if (!group.isMember(userId)) {
            throw new InsufficientPermissionsException("Devi essere membro del gruppo per modificare l'itinerario");
        }

        // Trova l'itinerario
        Itinerary itinerary = itineraryRepository.findByIdOptional(itineraryId)
            .orElseThrow(() -> new ItineraryNotFoundException(itineraryId));

        // Verifica che l'itinerario appartenga al gruppo
        if (!itinerary.group.id.equals(groupId)) {
            throw new ItineraryNotFoundException("L'itinerario non appartiene a questo gruppo");
        }

        // Aggiorna l'itinerario
        itineraryMapper.updateEntityFromRequest(request, itinerary);

        return itineraryMapper.toDto(itinerary);
    }

    /**
     * Elimina uno specifico itinerario per ID
     */
    @Transactional
    public void deleteItineraryById(Long groupId, Long itineraryId, Long userId) {
        // Verifica che il gruppo esista
        Group group = groupRepository.findByIdOptional(groupId)
            .orElseThrow(() -> new GroupNotFoundException(groupId));

        // Verifica che l'utente sia admin del gruppo
        if (!group.isAdmin(userId)) {
            throw new InsufficientPermissionsException("Solo gli admin possono eliminare l'itinerario");
        }

        // Trova l'itinerario
        Itinerary itinerary = itineraryRepository.findByIdOptional(itineraryId)
            .orElseThrow(() -> new ItineraryNotFoundException(itineraryId));

        // Verifica che l'itinerario appartenga al gruppo
        if (!itinerary.group.id.equals(groupId)) {
            throw new ItineraryNotFoundException("L'itinerario non appartiene a questo gruppo");
        }

        // Elimina l'itinerario (CASCADE elimina automaticamente le attività)
        itineraryRepository.delete(itinerary);
    }
}
