package com.team.flowershop.service
import com.team.flowershop.domain.Delivery
import java.util.Optional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Service Interface for managing [Delivery].
 */
interface DeliveryService {

    /**
     * Save a delivery.
     *
     * @param delivery the entity to save.
     * @return the persisted entity.
     */
    fun save(delivery: Delivery): Delivery

    /**
     * Get all the deliveries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAll(pageable: Pageable): Page<Delivery>

    /**
     * Get the "id" delivery.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<Delivery>

    /**
     * Delete the "id" delivery.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)

    /**
     * Search for the delivery corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun search(query: String, pageable: Pageable): Page<Delivery>
}
