package com.team.flowershop.service
import com.team.flowershop.domain.Collection
import java.util.Optional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Service Interface for managing [Collection].
 */
interface CollectionService {

    /**
     * Save a collection.
     *
     * @param collection the entity to save.
     * @return the persisted entity.
     */
    fun save(collection: Collection): Collection

    /**
     * Get all the collections.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAll(pageable: Pageable): Page<Collection>

    /**
     * Get all the collections with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    fun findAllWithEagerRelationships(pageable: Pageable): Page<Collection>
    /**
     * Get the "id" collection.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<Collection>

    /**
     * Delete the "id" collection.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)

    /**
     * Search for the collection corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun search(query: String, pageable: Pageable): Page<Collection>
}
