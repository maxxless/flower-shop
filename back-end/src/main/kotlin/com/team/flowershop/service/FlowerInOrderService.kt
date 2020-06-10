package com.team.flowershop.service
import com.team.flowershop.domain.FlowerInOrder
import java.util.Optional

/**
 * Service Interface for managing [FlowerInOrder].
 */
interface FlowerInOrderService {

    /**
     * Save a flowerInOrder.
     *
     * @param flowerInOrder the entity to save.
     * @return the persisted entity.
     */
    fun save(flowerInOrder: FlowerInOrder): FlowerInOrder

    /**
     * Get all the flowerInOrders.
     *
     * @return the list of entities.
     */
    fun findAll(): MutableList<FlowerInOrder>

    /**
     * Get the "id" flowerInOrder.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<FlowerInOrder>

    /**
     * Delete the "id" flowerInOrder.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)

    /**
     * Search for the flowerInOrder corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @return the list of entities.
     */
    fun search(query: String): MutableList<FlowerInOrder>
}
