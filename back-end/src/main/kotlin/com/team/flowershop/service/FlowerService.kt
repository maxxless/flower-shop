package com.team.flowershop.service
import com.team.flowershop.domain.Flower
import java.util.Optional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Service Interface for managing [Flower].
 */
interface FlowerService {

    /**
     * Save a flower.
     *
     * @param flower the entity to save.
     * @return the persisted entity.
     */
    fun save(flower: Flower): Flower

    /**
     * Get all the flowers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAll(pageable: Pageable): Page<Flower>

    /**
     * Get all the flowers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    fun findAllWithEagerRelationships(pageable: Pageable): Page<Flower>
    /**
     * Get the "id" flower.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<Flower>

    /**
     * Delete the "id" flower.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)

    /**
     * Search for the flower corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun search(query: String, pageable: Pageable): Page<Flower>
}
