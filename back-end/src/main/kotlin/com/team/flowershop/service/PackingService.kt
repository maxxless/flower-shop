package com.team.flowershop.service
import com.team.flowershop.domain.Packing
import java.util.Optional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Service Interface for managing [Packing].
 */
interface PackingService {

    /**
     * Save a packing.
     *
     * @param packing the entity to save.
     * @return the persisted entity.
     */
    fun save(packing: Packing): Packing

    /**
     * Get all the packings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAll(pageable: Pageable): Page<Packing>

    /**
     * Get the "id" packing.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<Packing>

    /**
     * Delete the "id" packing.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)

    /**
     * Search for the packing corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun search(query: String, pageable: Pageable): Page<Packing>
}
